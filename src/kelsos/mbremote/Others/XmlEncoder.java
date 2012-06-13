package kelsos.mbremote.Others;

public class XmlEncoder {
    /**
     * Given a String the function converts the xml unsafe characters to xml safe characters and returns the encoded
     * string.
     * @param text Original text.
     * @return xml safe text.
     */
    public static String encode(String text) {
        text = text.replace("&", "&amp;");
        text = text.replace("<", "&lt;");
        text = text.replace(">", "&gt;");
        text = text.replace("\"", "&quot;");
        text = text.replace("\'", "&apos;");
        return text;
    }
}
