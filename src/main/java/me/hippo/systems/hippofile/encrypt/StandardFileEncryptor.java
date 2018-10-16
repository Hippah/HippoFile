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

package me.hippo.systems.hippofile.encrypt;

import me.hippo.systems.hippofile.service.HippoFileService;

import java.util.HashMap;

/**
 * @author Hippo
 * @since 10/15/2018
 */
public enum StandardFileEncryptor implements Encrypter{

    /**
     * The instance to {@link StandardFileEncryptor}.
     */
    INSTANCE;

    /**
     * @inheritDoc
     * @see Encrypter#encrypt(String)
     */
    @Override
    public String encrypt(final String content) {
        final HashMap<Integer, Character> encryptedMap = new HashMap<>();

        final StringBuilder encrypted = new StringBuilder();
        for(int index = 0; index < content.length(); index++){
            final char character = content.charAt(index);
            int swapIndex = index + 1;

            if(!encryptedMap.containsKey(index)) {
                if (swapIndex < content.length()) {
                    final char swap = content.charAt(swapIndex);
                    encryptedMap.put(swapIndex, character);
                    encryptedMap.put(index, swap);
                }
            }
        }

        for(final char encryptedCharacter : encryptedMap.values()){
            encrypted.append(encryptedCharacter);
        }

        return encrypted.reverse().toString();
    }
    /**
     * @inheritDoc
     * @see Encrypter#decrypt(String)
     */
    @Override
    public String decrypt(final String content) {
        final HashMap<Integer, Character> encryptedMap = HippoFileService.getEncryptedMap(content);
        final HashMap<Integer, Character> decryptedMap = new HashMap<>();

        final StringBuilder decrypted = new StringBuilder();
        for(int index = 0; index < content.length(); index++) {
            final char character = encryptedMap.get(index);

            int swapIndex = index + 1;

            if (!decryptedMap.containsKey(index)) {
                if (swapIndex < content.length()) {
                    final char swap = encryptedMap.get(swapIndex);
                    decryptedMap.put(swapIndex, character);
                    decryptedMap.put(index, swap);
                }
            }
        }

        for(final char decryptedChar : decryptedMap.values()){
            decrypted.append(decryptedChar);
        }

        return decrypted.reverse().toString();
    }
}
