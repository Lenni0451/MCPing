package net.lenni0451.mcping.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class LenientTypeAdapter<T> extends TypeAdapter<T> {

    private final TypeAdapter<T> delegate;

    public LenientTypeAdapter(final TypeAdapter<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        this.delegate.write(out, value);
    }

    @Override
    public T read(JsonReader in) throws IOException {
        try {
            return this.delegate.read(in);
        } catch (Exception e) {
            in.skipValue();
            return null;
        }
    }

}
