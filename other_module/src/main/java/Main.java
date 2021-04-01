public class Main {

    public static final int COUNT_ITERATION = 5;
    private final static StringBuilder stringBuilder = new StringBuilder();

    private static void setWait() {
        try {
            stringBuilder.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void cycleForThread(String str, int divider) {
        for (int i = 0; i < COUNT_ITERATION; i++) {
            if(stringBuilder.length()%3 != divider) setWait();
            stringBuilder.append(str);
            stringBuilder.notifyAll();
            if(i < COUNT_ITERATION - 1) setWait();
        }
    }

    public static void main(String[] args) {

        Thread threadFirst = new Thread(() -> {
            synchronized (stringBuilder) {
                cycleForThread("A", 0);
            }
        });

        Thread threadSecond = new Thread(() -> {
            synchronized (stringBuilder) {
                cycleForThread("B", 1);
            }
        });

        Thread threadThird = new Thread(() -> {
            synchronized (stringBuilder) {
                cycleForThread("C", 2);
            }
        });

        threadSecond.start();
        threadThird.start();
        threadFirst.start();

        try {
            threadFirst.join();
            threadSecond.join();
            threadThird.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.err.println(stringBuilder.toString());
    }
}