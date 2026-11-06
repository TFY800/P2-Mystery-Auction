package auction.persistence;

import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.util.Base64;

final class TextFormat {
    private TextFormat() { }
    static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
    static String decode(String value) {
        try {
            return StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(Base64.getDecoder().decode(value))).toString();
        } catch (CharacterCodingException e) {
            throw new IllegalArgumentException("Invalid UTF-8 text.", e);
        }
    }
    static String[] fields(String line, int count) {
        if (line == null) throw new IllegalArgumentException("Unexpected end of file.");
        String[] fields = line.split("\\t", -1);
        if (fields.length != count) throw new IllegalArgumentException("Invalid field count.");
        return fields;
    }
}
