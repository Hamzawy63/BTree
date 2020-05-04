package eg.edu.alexu.csd.filestructure.btree;

public class InputChecker {
    static void checkNullValue(Object... input) {
        for (Object o : input)
            if (o == null) LocalException.throwRunTimeErrorException();
    }
    static void checkEmptyString(String ...input) {
        for(String s : input) {
            if(s.isEmpty()) LocalException.throwRunTimeErrorException();
        }
    }
}
