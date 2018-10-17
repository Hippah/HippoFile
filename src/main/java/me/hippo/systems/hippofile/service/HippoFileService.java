/*
 * MIT License
 *
 * Copyright (c) 2018 Hippo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.hippo.systems.hippofile.service;

import me.hippo.systems.hippofile.HippoFile;
import me.hippo.systems.hippofile.encrypt.Encrypter;
import me.hippo.systems.hippofile.exception.HippoFileException;
import me.hippo.systems.hippofile.object.HippoFileObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Hippo
 * @since 10/15/2018
 */
public final class HippoFileService {

    /**
     * Gets an already existing {@link HippoFile}.
     * @param path  The path to the files.
     * @param encrypters  The encrypters needed to decrypt the file.
     * @return  The file.
     * @throws IOException  If the reading of the file fails.
     * @throws HippoFileException  If the reading of the elements fails.
     */
    public static HippoFile getFile(final String path, final Encrypter... encrypters) throws IOException, HippoFileException {
        return getFile(new File(path), encrypters);
    }

    /**
     * @see #getFile(String, Encrypter...)
     */
    public static HippoFile getFile(final File path, final Encrypter... encrypters) throws IOException, HippoFileException {
        final ArrayList<String> lines = new ArrayList<>();
        if(encrypters.length > 0){
            final StringBuilder content = new StringBuilder();
            for(final String line : Files.readAllLines(path.toPath())){
                content.append(line).append("\n");
            }
            String decrypt = "";
            for(final Encrypter encrypter : encrypters){
                decrypt = encrypter.decrypt(content.toString());
            }
            lines.addAll(Arrays.asList(decrypt.split("\n")));
        }else {
            lines.addAll(Files.readAllLines(path.toPath()));
        }

        final String[] dirs = path.getAbsolutePath().split("/");
        String fileName = "";
        final StringBuilder pathBuilder = new StringBuilder();
        for(final String dir : dirs){
            if(dir.endsWith(".hippo")){
                fileName = dir.substring(0, dir.length() - 6);
            }else{
                pathBuilder.append(dir).append("/");
            }
        }
        final HippoFile hippoFile = new HippoFile().setName(fileName).setDestination(pathBuilder.toString());

        for(final String line : lines){
            final StringBuilder nameBuilder = new StringBuilder();
            int startObject = 0;
            for(final char name : line.toCharArray()){
                if(name == '{'){
                    break;
                }
                nameBuilder.append(name);
                startObject++;
            }
            final HippoFileObject hippoFileObject = new HippoFileObject(nameBuilder.toString());
            int subElements = 0;
            for(int i = startObject; i < line.length(); i++){
                final char character = line.charAt(i);
                if(character == '{'){
                    subElements++;
                }
                if(character == '}'){
                    if(subElements == 0){
                        break;
                    }
                    subElements--;
                }
            }
            final ArrayList<Character> elementList = new ArrayList<>();
            for(int i = startObject + 1; i < line.length(); i++) {
                final char character = line.charAt(i);
                elementList.add(character);
             }
            final char[] elementChars = new char[elementList.size() - 1];
            for(int i = 0; i < elementList.size() - 1; i++){

                elementChars[i] = elementList.get(i);
            }

            hippoFileObject.scanElements(elementChars);

           hippoFile.addObject(hippoFileObject);
        }
        return hippoFile;
    }

    /**
     * Converts a {@link HippoFile} to a {@link String}.
     * @param hippoFile  The hippo file to convert.
     * @return  The converted file.
     */
    public static String convertToText(final HippoFile hippoFile) {
        final StringBuilder content = new StringBuilder();
        for(final HippoFileObject object : hippoFile.getContent()){
            content.append(object.getContent());
        }
        return content.toString();
    }

    /**
     * Gets the encrypted mappings for a {@link String}.
     * @param content  The content to make the mappings from.
     * @return  The mappings.
     */
    public static HashMap<Integer, Character> getEncryptedMap(final String content) {
        final HashMap<Integer, Character> encryptedMap = new HashMap<>();
        for(int i = 0; i < content.length(); i++){
            encryptedMap.put(i, content.charAt(i));
        }
        return encryptedMap;
    }
}
