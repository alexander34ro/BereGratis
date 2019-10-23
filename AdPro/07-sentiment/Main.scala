// Advanced Programming. Andrzej Wasowski. IT University
// To execute this example, run "sbt run" or "sbt test" in the root dir of the project
// Spark needs not to be installed (sbt takes care of it)

import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._

import scala.collection.mutable.ArrayBuffer

// This
// (https://stackoverflow.com/questions/40015416/spark-unable-to-load-native-hadoop-library-for-your-platform)
// actually does seems to work, to eliminate the missing hadoop message.
// 'WARN NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable'
// AW not sure if the 'hadoop missing warning' matters though.

object Main {

  type Embedding = (String, List[Double])
  type ParsedReview = (Integer, String, Double)
  System.setProperty("hadoop.home.dir", "F:/Master/ADPRO/hadoop-2.6.0")

  org.apache.log4j.Logger getLogger "org" setLevel (org.apache.log4j.Level.WARN)
  org.apache.log4j.Logger getLogger "akka" setLevel (org.apache.log4j.Level.WARN)
  val spark: SparkSession = SparkSession.builder
    .appName("Sentiment")
    .master("local[5]")
    .getOrCreate

  spark.conf.set("spark.executor.memory", "4g")

  import spark.implicits._

  val reviewSchema = StructType(
    Array(
      StructField("reviewText", StringType, nullable = false),
      StructField("overall", DoubleType, nullable = false),
      StructField("summary", StringType, nullable = false)
    )
  )

  // Read file and merge the text and summary into a single text column

  def loadReviews(path: String): Dataset[ParsedReview] =
    spark.read
      .schema(reviewSchema)
      .json(path)
      .rdd
      .zipWithUniqueId
      .map[(Integer, String, Double)] {
        case (row, id) =>
          (id.toInt, s"${row getString 2} ${row getString 0}", row getDouble 1)
      }
      .toDS
      .limit(500)
      .withColumnRenamed("_1", "id")
      .withColumnRenamed("_2", "text")
      .withColumnRenamed("_3", "overall")
      .as[ParsedReview]

  // Load the GLoVe embeddings file

  def loadGlove(path: String): Dataset[Embedding] =
    spark.read
      .text(path)
      .map { _ getString 0 split " " }
      .map(r => (r.head, r.tail.toList.map(_.toDouble))) // yuck!
      .withColumnRenamed("_1", "word")
      .withColumnRenamed("_2", "vec")
      .as[Embedding]

  def main(args: Array[String]): Unit = {

    val DATA_PATH = "src/main/scala/data/"

    val glove = loadGlove(s"${DATA_PATH}/glove.6B.50d.txt")
    val reviews = loadReviews(s"${DATA_PATH}/reviews_small_1.json")

    // replace the following with the project code
    // glove.show
    // reviews.show

    // Train the sentiment perceptron here (this is *one* possible workflow, and it is slow ...)
    //
    //   - First clean the data
    //      - Use the tokenizer to turn records with reviews into records with
    //      lists of words
    //         documentation: https://spark.apache.org/docs/latest/ml-features.html#tokenizer
    //         output type: a collection of (Integer, Seq[String], Double)

    val tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
    val tokenized =
      tokenizer.transform(reviews).select("id", "words", "overall")
    // tokenized.show

    //  - Second translate the reviews to embeddings
    //      - Flatten the list to contain single words
    //         output type: a collection (Integer, String, Double) but much
    //         longer than input

    val expanded = tokenized
      .withColumn("word", explode($"words"))
      .select("id", "word", "overall")
    // expanded.show

    //      - Join the glove vectors with the triples
    //         output type: a collection (Integer, String, Double,
    //         Array[Double])
    //      - Drop the word column, we don't need it anymore
    //         output type: a collection (Integer, Double, Array[Double])

    val joined = expanded.join(glove, "word").select("id", "overall", "vec")
    // joined.show

    //      - Add a column of 1s
    //         output type: a collection (Integer, Double, Array[Double], Integer)

    val with_ones = joined.withColumn("ones", lit(1))
    // with_ones.show

    //      - Reduce By Key (using the first or two first columns as Key), summing the last column
    //         output type: a collection (Integer, Double, Array[Double], Integer)
    //         (just much shorter this time)

    val summed = with_ones.groupBy($"id").sum("ones")
    // summed.show
    val reduced =
      summed.join(joined, "id").select("id", "overall", "vec", "sum(ones)")
    //reduced.show

    //      - In each row divide the Array (vector) by the count (the last column)
    //         output type: a collection (Integer, Double, Array[Double])
    //         This is the input for the classifier training

    // val divided = reduced
    // 	.map(row => {
    // 		val sum = row.getAs[Long]("sum(ones)")
    // 		(
    // 			row.getAs[Int]("id"),
    // 			row.getAs[Double]("overall"),
    // 			row.getAs[ArrayBuffer[Double]]("vec").map(_ / sum)
    // 		)
    // 	})
    // 	.withColumnRenamed("_1", "id")
    // 	.withColumnRenamed("_2", "overall")
    // 	.withColumnRenamed("_3", "vec")
    // divided.show

    //  - Train the perceptron:
    //      - translated the ratings from 1..5 to 1..3 (use map)
    //      - make sure tha columns are named "id", "label", "features"

    val ratings =
      Map(1.0 -> 1.0, 2.0 -> 1.0, 3.0 -> 2.0, 4.0 -> 3.0, 5.0 -> 3.0)
    val mapped = reduced
      .map(row => {
        val rating = ratings(row.getAs[Double]("overall"))
        val sum = row.getAs[Long]("sum(ones)")
        (
          row.getAs[Int]("id"),
          rating,
          row.getAs[ArrayBuffer[Double]]("vec").map(_ / sum)
        )
      })
      .withColumnRenamed("_1", "id")
      .withColumnRenamed("_2", "label")
      .withColumnRenamed("_3", "features")
    mapped.show

    //      - follow the MultilayerPerceptronClassifier tutorial.
    //      - Remember that the first layer needs to be #50 (for vectors of size
    //      50), and the last needs to be #3.
    //  - Validate the perceptron
    //      - Either implement your own validation loop  or use
    //        org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
    //
    // Any suggestions of improvement to the above guide are welcomed by
    // teachers.
    //
    // This is an open programming exercise, you do not need to follow the above
    // guide to complete it.

    spark.stop
  }

}
