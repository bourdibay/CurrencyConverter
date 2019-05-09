package com.bourdi_bay.currencyconverter;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

class CurrenciesFile {

    private Context m_context;
    private String m_filename;

    CurrenciesFile(Context context, String filename) {
        m_context = context;
        m_filename = filename;
    }

    void save(String content) {
        FileOutputStream outputStream;
        try {
            outputStream = m_context.openFileOutput(m_filename, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String load() throws IOException {
        FileInputStream inputStream = m_context.openFileInput(m_filename);
        StringBuilder fileContent = new StringBuilder();
        byte[] buffer = new byte[1024];
        int n;
        while ((n = inputStream.read(buffer)) != -1) {
            fileContent.append(new String(buffer, 0, n));
        }
        return fileContent.toString();
    }
}
