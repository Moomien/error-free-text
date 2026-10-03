package io.github.moomien.errorfreetext.correction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TextSplitter {
    private final int maxChunkLength;

    public TextSplitter(int maxChunkLength) {
        if (maxChunkLength < 2){
            throw new IllegalArgumentException("maxChunkLength must be >= 2");
        }
        this.maxChunkLength = maxChunkLength;
    }

    public List<String> split(String text) {
        List<String> chunks = new ArrayList<>();
        int start = 0;

        while(text.length() - start > maxChunkLength) {
            int end = findCutIndex(text, start);
            chunks.add(text.substring(start, end));
            start = end;
        }
        if (start < text.length()) {
            chunks.add(text.substring(start));
        }
        return chunks;
    }

    private int findCutIndex(String text, int start) {
        int limit = start + maxChunkLength;

        for (int i = limit; i > start; i--) {
            if (Character.isWhitespace(text.charAt(i - 1))){
                return i;
            }
        }

        return Character.isHighSurrogate(text.charAt(limit - 1)) ? limit - 1 : limit;
    }
}
