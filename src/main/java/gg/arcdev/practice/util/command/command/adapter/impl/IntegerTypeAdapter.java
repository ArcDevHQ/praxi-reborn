package gg.arcdev.practice.util.command.command.adapter.impl;

import gg.arcdev.practice.util.command.command.adapter.CommandTypeAdapter;

public class IntegerTypeAdapter implements CommandTypeAdapter
{
    @Override
    public <T> T convert(final String string, final Class<T> type) {
        return type.cast(Integer.parseInt(string));
    }
}
