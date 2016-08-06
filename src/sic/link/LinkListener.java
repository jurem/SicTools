package sic.link;

import java.io.File;

public interface LinkListener {

    void onLinked(File f, String message);
}
