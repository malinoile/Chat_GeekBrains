import java.util.Arrays;

public class Lesson6 {

    private static final int SEARCHING_NUMBER = 4;

    private static final int CHECK_INT_1 = 1;
    private static final int CHECK_INT_2 = 4;

    public static void main(String[] args) {

    }

    public int[] getSubArray(int[] array) {
        int index = -1;
        for (int i = array.length - 1; i >= 0; i--) {
            if(array[i] == SEARCHING_NUMBER) {
                index = i;
                break;
            }
        }
        if(index < 0) throw new RuntimeException();

        return Arrays.copyOfRange(array, index+1, array.length);
    }

    public boolean checkArray(int[] array) {
        boolean checkedNumberOne = false;
        boolean checkedNumberTwo = false;

        for (int i = 0; i < array.length; i++) {
            if(array[i] == CHECK_INT_1 && !checkedNumberOne) {
                checkedNumberOne = true;
            } else if(array[i] == CHECK_INT_2 && !checkedNumberTwo) {
                checkedNumberTwo = true;
            }
        }

        return checkedNumberOne && checkedNumberTwo;
    }
}
