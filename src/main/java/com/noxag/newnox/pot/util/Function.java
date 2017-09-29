package com.noxag.newnox.pot.util;

import java.io.IOException;

@FunctionalInterface
public interface Function<One, Two, Three, Four> {
    public Four apply(One one, Two two, Three three) throws IOException;
}
