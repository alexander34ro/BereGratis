class Printer {
    public static void print() {
        synchronized (Printer.class) {
            System.out.print("-");
            try { Thread.sleep(50); } catch (InterruptedException exn) { }
            System.out.print("|");
        }
    }

    public static void main(String[] args) {

        Thread t1 = new Thread(){
            @Override
            public void run() {
                while(true){
                    Printer.print();
                }
            }
        };

        Thread t2 = new Thread(){
            @Override
            public void run() {
                while(true){
                    Printer.print();
                }
            }
        };

        t1.start();
        t2.start();
    }
}
