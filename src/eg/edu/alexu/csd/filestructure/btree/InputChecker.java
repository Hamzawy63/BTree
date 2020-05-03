package eg.edu.alexu.csd.filestructure.btree;

public class InputChecker {
    static void checkNullValue(Object... input) {
        for (Object o : input)
            if (o == null) LocalException.throwRunTimeErrorException();
    }
}
