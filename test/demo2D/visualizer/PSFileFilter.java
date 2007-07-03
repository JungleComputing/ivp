import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

public class PSFileFilter extends FileFilter {

    public boolean accept(File f) {
		return true;
    }

    //The description of this filter
    public String getDescription() {
        return "PostScript (*.PS)";
    }
}