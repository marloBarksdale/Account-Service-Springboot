// do not remove imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;





class ArrayUtils {
    // define invert method here




    public static <T> T[] invert(T[] arr){

        int j= arr.length-1;
        int i=0;

        while(i < j){


            T temp = arr[i];
            arr[i++] = arr[j];
            arr[j--] = temp;
        }

        return arr;
    }
}