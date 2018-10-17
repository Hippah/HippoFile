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

package me.hippo.systems.hippofile;

import me.hippo.systems.hippofile.encrypt.Encrypter;
import me.hippo.systems.hippofile.exception.HippoFileException;
import me.hippo.systems.hippofile.object.HippoFileObject;
import me.hippo.systems.hippofile.service.HippoFileService;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Hippo
 * @since 10/15/2018
 */
public final class HippoFile {

    /**
     * The name of the file.
     */
    private String name;

    /**
     * The destination of the file.
     * <p>
     *     The destination does not include {@code name}.hippo
     * </p>
     */
    private File destination;

    /**
     * The path of the file.
     * <p>
     *     Includes the destination and {@code name}.hippo
     * </p>
     */
    private File path;

    /**
     * An {@link CopyOnWriteArrayList} of {@link HippoFileObject} to store all of the files content.
     */
    private final CopyOnWriteArrayList<HippoFileObject> content = new CopyOnWriteArrayList<>();

    /**
     * Sets the name of the file.
     * <p>
     *     If the {@code destination} is not {@code null} it will make the {@code path}.
     * </p>
     * @param name  The name.
     * @return  The hippo file.
     * @throws IOException  If the creation of {@code path} fails.
     */
    public HippoFile setName(final String name) throws IOException {
        this.name = name;
        if(destination != null){
            this.path = new File(destination, name + ".hippo");
            if(!this.path.exists()){
                System.out.println(path);
                if(!path.createNewFile()){
                    throw new IOException("Creation of hippo file failed!");
                }
            }
        }
        return this;
    }

    /**
     * Sets the destination.
     * <p>
     *     If the {@code name} is not {@code null} it will make the {@code path}.
     * </p>
     * @param destination  The destination.
     * @return  The hippo file.
     * @throws IOException  if the creation of {@code destination} fails.
     * @throws IOException  If the creation of {@code path} fails.
     */
    public HippoFile setDestination(final String destination) throws IOException {
        this.destination = new File(destination);
        if(!this.destination.exists()){
            if(!this.destination.mkdirs()){
                throw new IOException("Creation of the hippo file destination failed!");
            }
        }
        if(name != null){
            this.path = new File(destination, name + ".hippo");
            if(!this.path.exists()){
                if(!path.createNewFile()){
                    throw new IOException("Creation of hippo file failed!");
                }
            }
        }
        return this;
    }

    /**
     * Adds a {@link HippoFileObject} to {@code content}.
     * @param hippoFileObject  The hippo file object to add.
     * @return  The hippo file.
     */
    public HippoFile addObject(final HippoFileObject hippoFileObject){
        content.add(hippoFileObject);
        return this;
    }

    /**
     * Saves the hippo file.
     * @return The hippo file.
     * @throws IOException  {@code path} does not exist.
     */
    public HippoFile save() throws IOException {
        final FileWriter fileWriter = new FileWriter(path);
        for(final HippoFileObject hippoFileObject : content){
            fileWriter.write(hippoFileObject.getContent());
        }
        fileWriter.close();
        return this;
    }

    /**
     * Clears the hippo file.
     * <p>
     *     Removes everything.
     * </p>
     * @throws IOException  {@code path} does not exist.
     */
    public void clear() throws IOException{
        final FileWriter fileWriter = new FileWriter(getPath());
        fileWriter.close();
    }

    /**
     * Checks if the file is empty.
     * @return  If the file is empty.
     * @throws IOException  If the reading fails.
     */
    public boolean isEmpty() throws IOException {
        final FileReader fileReader = new FileReader(path);
        final int read = fileReader.read();
        fileReader.close();
        return read == -1;
    }

    /**
     * Encrypts the file with {@link me.hippo.systems.hippofile.encrypt.StandardFileEncryptor}.
     * @return  The hippo file.
     * @throws IOException  If {@code path} does not exist.
     */
    public HippoFile encrypt() throws IOException {
        return encrypt(Encrypter.standard());
    }

    /**
     * Encrypts the file with the specified {@link Encrypter}s.
     * @param encrypters  All the encrypters used to encrypt the file.
     * @return  The hippo file.
     * @throws IOException  If {@code path} does not exist.
     */
    public HippoFile encrypt(final Encrypter... encrypters) throws IOException {
        clear();
        final String content = HippoFileService.convertToText(this);
        final FileWriter fileWriter = new FileWriter(path);
        for(final Encrypter encrypter : encrypters){
            fileWriter.write(encrypter.encrypt(content));
        }
        fileWriter.close();
        return this;
    }

    /**
     * Gets a {@link HippoFileObject} by its name.
     * @param object  The {@code object} to get.
     * @return  The {@code object}.
     * @throws HippoFileException  If the {@code object} doesn't exist.
     */
    public HippoFileObject getObject(final String object) throws HippoFileException {
        for(final HippoFileObject hippoFileObject : content){
            if(hippoFileObject.getName().equalsIgnoreCase(object)){
                return hippoFileObject;
            }
        }
        throw new HippoFileException("An exception was thrown whilst finding object!\n\nDetails:\nFile Name: " + name + "\nObject Name: " + object + "\nFound: null\n\nMaybe the object does not exist?");
    }

    /**
     * Gets the {@code name}.
     * @return  The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the {@code destination}.
     * @return  The destination.
     */
    public File getDestination() {
        return destination;
    }

    /**
     * Gets the {@code path}.
     * @return  The path.
     */
    public File getPath(){
        return path;
    }

    /**
     * Gets the {@code content}.
     * @return  The content.
     */
    public CopyOnWriteArrayList<HippoFileObject> getContent() {
        return content;
    }
}
