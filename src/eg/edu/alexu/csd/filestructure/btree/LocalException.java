package eg.edu.alexu.csd.filestructure.btree;

import javax.management.RuntimeErrorException;

public class LocalException {
     static void throwRunTimeErrorException() {
        throw new RuntimeErrorException(new Error());
    }
}
