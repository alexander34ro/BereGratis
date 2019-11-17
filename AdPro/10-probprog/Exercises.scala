// Advanced Programming, Andrzej Wasowski
// Probabilistic Programming (AKA Probability is also a monad)

// Group number: 11
//
// AUTHOR1: Alex Ardelean
// TIME1: 5 <- how much time have you used on solving this exercise set
// (excluding reading the book, fetching pizza, and going out for a smoke)
//
// AUTHOR2: __________
// TIME2: _____ <- how much time have you used on solving this exercise set
// (excluding reading the book, fetching pizza, and going out for a smoke)
//
// You should work with the file by following the associated exercise sheet
// (available in PDF from the course website).
//
// This file is compiled with 'sbt compile' and tested with 'sbt test'.
//
// The file shall always compile and run after you are done with each exercise
// (if you do them in order).  Please compile and test frequently. Of course,
// some tests will be failing until you finish.  One will be failing after you
// finish.

package adpro

import com.cra.figaro.language.{Element, Constant, Flip, Universe, Select}
import com.cra.figaro.library.compound._
import com.cra.figaro.library.atomic.continuous.{Beta, AtomicBeta}
import com.cra.figaro.library.atomic.discrete.{Binomial,Uniform}
import com.cra.figaro.algorithm.ProbQueryAlgorithm
import com.cra.figaro.algorithm.sampling.{Importance}
import com.cra.figaro.algorithm.factored.{VariableElimination}
import scala.collection.Map

// All the exercise text is included below in comments (no PDF file)

// Probabilistic programs are hard to test, so the tests we have are a bit weak.
// I hope you appreciate that we have them :)  Also note, that more exercises
// you solve the slower the test suite becomes, due to many samplings it runs.
// You may want to disable tests temporarily to speed up work.
//
// For many exercises, you are (quietely) expected to run the queries in the
// REPL in order to inspect probability values (or to print them to the standard
// output).
//
// Hand in the completed file (this file, Exercises.scala). No zip files, no
// pdfs, etc.
//
// The main inspiration for this exercise comes from the material of a
// probabilistic programming course by Joost-Pieter Katoen at RWTH Aachen,
// Germany.

object Exercises {

  // Before starting to solve the exercises below, please study the file
  // Basic.scala, side by side with this week's slides. Once you understood the
  // code in Basic.scala, come back here.

  // Peter and Paula play a game.  An urn contains some black balls and a single
  // red ball. They take turns taking one random ball out of the urn. The first
  // one to pick the red ball wins.
  //
  // We model the players using a case class (one of many possible options):

  sealed trait Player
  case object Peter extends Player
  case object Paula extends Player

  // And we add a function to determine the next player (a simple alternation):

  def next (player: Player): Player = player match {
    case Peter => Paula
    case Paula => Peter
  }

  // The number of balls, including exactly 1 red ball, found in the run at the
  // begining of the game

  val BallsNo: Int = 8

  // Exercise 1.
  //
  // Write a function pick that given the number 'n' of black balls in the urn
  // (there is always one red inside the urn, when a move is made) returns a
  // probability distribution that gives true with the probability of picking
  // the red ball. We model the outcome of picking as true, iff the red ball has
  // been picked, and false otherwise. Use the following constructor 'Flip' to
  // implement 'pick'.
  //
  // Flip (probability: Double): Element[Boolean]

  def pick (n: Int): Element[Boolean] = Flip (1.0 / (n + 1))

  // Exercise 2.
  //
  // Write a function 'move' that given the initial player and the number of
  // black balls 'n' present in the urn returns the probability distribution
  // defining which player wins.
  //
  // Hint: Andrzej's solution used 'pick' and 'Constant':
  //
  // Constant[A] (a: A) :Element[A]
  //
  // This constructor returns a distribution where the value 'a' has probability
  // '1'.

  def move (player: Player, n: Int): Element[Player] = {
    for {
      p <- pick(n)
      w <- if (p) Constant(player) else move(next(player), n - 1)
    } yield w
    // faster solution
//    val p = if (n % 2 == 0) (1 + n/2) / (n + 1.0) else 0.5
//    for {
//      c <- Flip(p)
//      w <- if (c) Constant(player) else Constant(next(player))
//    } yield w
  }

  // Exercise 3.
  //
  // Peter is polite and offers a choice to Paula, if she wants to start or
  // rather had that he started.
  //
  // Use the function 'move' to estimate the chance of Paula winning
  // when she starts, and when Peter starts, if the urn contains 'BallsNo' balls
  // in total (including one red).   The function 'Importance.probability' takes
  // two arguments: a distribution and a value from its range. It returns the
  // estimate of probability that the distribution takes this value.
  //
  // Importance.probability[A] (distribution: Element[A], value: A): Double

  // Probability that Paula wins given Paula starts (the total no of balls: BallsNo)
  def probPaula: Double = {
    Importance.probability(move(Paula, BallsNo), Paula)
  }

  // Probability that Paula wins given Peter starts (the total no of balls: BallsNo)
  def probPeter: Double = {
    1.0 - probPaula
  }

  // Which strategy is beter for Paula? What if BallsNo == 9?
  //
  // Write your answer here in a comment:
  // Trick question, if BallsNo is odd then they have equal chances
  // Otherwise, going first seems to always be the better choice
  // But doesn't really matter for high numbers of balls


  // Exercise 4.
  //
  // A quick pen-and-pencil question: Can you estimate the size of
  // the Bayesian network generated by 'move (p, 10)' for some player constant p?
  //
  // Write your answer here in a comment:
  // 2 ^ 10
  //
  // Observe, that this model would be very annoying and laborious to build
  // manually on paper, but with statistical interpretation in a programming
  // framework we can build models for 200 balls easily.  This is probably the
  // main strength of probabilistic programming.
  //
  // You do not need to write the answer to this question for grading.
  // Use it yourself to appreciate the power of the probabilistic programming
  // tool).



  // Exercise 5.
  //
  // We know that Paula has won the game.  What is the probability that she has
  // started the game?  Use MAP (maximum posterior probability), assuming that
  // it was initiall equally likely that Peter and Paula are starting.
  //
  // This exercise is split in a number of smaller steps.  You should try to get
  // an overview of the entire constructed model.
  //
  // We first create a uniform prior for the first mover:

  val firstMover = Uniform (Paula, Peter) // uniform prior

  // Now create a nullary function 'gameResult' that picks the first mover
  // randomly using 'firstMover' and then returns the probability distribution
  // for a game played with BallsNo balls in the urn:

  def gameResult: Element[Player] = ???

  // What is the probability that Paula wins with this uniform prior? Does it
  // agree with your intuition?
  //
  // _____

  // Now we are going to make the observation that Paula wins. Use the observe
  // function on the gameResult.  See documentation:
  // https://www.cra.com/Figaro_ScalaDoc/com/cra/figaro/language/Element.html#observe(observation:Element.this.Value):Unit
  //
  // This is unfortunately an impure call in Figaro, so let me make it, so you
  // are not getting used to it :)

  lazy val gameWonByPaula = gameResult
  // gameWonByPaula.observe (Paula)

  // ^-- Uncomment this when everything above works
  // Keeping this commented allows the testsuite to work while you are not done
  // above.

  // After the above all the probability queries on gameWonByPaula will be
  // performed under the condition that Paula has won.

  // Compute the probability that Paula has started
  def probPaulaStarted: Double = ???

  // Does this probability depend on the number of balls in the urn in the
  // urn being even or odd? What if it is even? What if it is odd?
  //
  // ____



  // Exercise 6.
  //
  // We know that winning player wins approximately 1/2 games when she
  // starts, and we know now (do you) that if there is an even number of balls
  // in the urn then the probability is precisely equal for both players, while
  // if the number of balls is odd the probability of the first player winning
  // is slightly higher.
  //
  // In this exercise, we assume that the number of balls is unknown, but it is
  // taken from range 1 to 6 with uniform probability (uniform prior) and we
  // will observe that Player1 has won.  We will ask what is the probability
  // that the urn held an odd number of balls in the begining of the game.  We
  // expect this probability to be slightly higher than 50%, as player 1 winning
  // makes as believe slightly that an odd number of balls are in the urn.

  // Let UpperBound will be the maximum number of balls in the urn that we
  // consider.

  val UpperBound: Int = 6

  // Construct a uniform prior on the number of black balls in the urn
  // from zero to UpperBound-1.
  //
  // Use the Uniform[A] constructor (a variadic function that takes all the
  // equally like values of A as its variable size argument list):
  //
  // Uniform[A] (a : A*) :Element[A]
  //
  // The argument list can be generated using List.tabulate[A].

  lazy val blackBallsNo: Element[Int] = ???

  // Now convert the prior distribution on the initial number of black balls in
  // the urn, into a distribution over the winning player.  Since the game is
  // entirely symmetric, we can assume that Paula is starting (the result for
  // Peter will be the same). Hint: flatMap
  //
  // There is no test for this step of the computation.

  def outcome: Element[Player] = ???

  // Uncomment the following to assert that the chances of winning by Paul and
  // Peter are equal

  // outcome observe (Paula)

  // ^-- When you uncomment this, the test on blackBallsNo will fail. This is
  // expected, as no longer all values are equally likely.  The prior turns into
  // a posterior, which is no longer uniform.  Just ignore that the test fails.
  // Keeping all these tests passing is fairly complex (this is largely caused
  // by the framework API being partly imperative, so tests are not independent
  // of later computations)

  // Now define the posterior probabilities for all size of the urn from 1 to
  // UpperBound. You can do this using Importance.probability and List.tabulate.
  // We need a slightly different version of the former that takes a predicate,
  // not a concrete value:
  //
  // Importance.probability[A] (distribution: Element[A], p: A => Boolean)
  //
  // This version returns the probability that the predicate p holds on the
  // values generated with 'distribution'.

  lazy val posteriorOdd: Double = ???

  // Is the posteriorOdd greater than 1/2?


  // Reflect whether the above estimation would take you more time analytically
  // or with a probabilistic programming library?

}
