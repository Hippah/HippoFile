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
import me.hippo.systems.hippofile.object.HippoFileElement;
import me.hippo.systems.hippofile.object.HippoFileObject;
import me.hippo.systems.hippofile.service.HippoFileService;

import java.io.IOException;

/**
 * @author Hippo
 * @since 10/15/2018
 * <p>
 *     Yes, this class is a little messy, but I just wanted to show most of the functionality of this system.
 * </p>
 */
public final class Main {

    /**
     * The main method.
     * @param args  Java args.
     */
    public static void main(final String[] args){
        try {
           createFiles();
           grabFiles();
        }catch (IOException  | HippoFileException e){
            e.printStackTrace();
        }

    }

    /**
     * Creates a new {@link HippoFile}.
     * @throws IOException  If the creation of the file fails.
     */
    private static void createFiles() throws IOException {
        System.out.println("#Creating a HippoFile: \n");

        final HippoFileElement hippoFileElement = new HippoFileElement("SomeElement", "SomeValue", 69, true);
        final HippoFileElement anotherElement = new HippoFileElement("AnotherElement", 69);

        final HippoFileObject hippoFileObject = new HippoFileObject("SomeObject")
                .addElement(hippoFileElement);
        final HippoFileObject anotherObject = new HippoFileObject("AnotherObject")
                .addElement(anotherElement);

        final HippoFile hippoFile = new HippoFile()
                .setName("Test")
                .setDestination("/home/hippo/Desktop/Systems/HippoFile/bin")
                .addObject(hippoFileObject)
                .addObject(anotherObject)
                .encrypt();

        for(final HippoFileObject content : hippoFile.getContent()){
            System.out.println(content.getContent() + "\n");
            for(final HippoFileElement element : content.getElements()){
                for(final Object value : element.getValues()){
                    System.out.println(value);
                }
            }
        }

    }

    /**
     * Grabs an already existing {@link HippoFile}.
     * @throws IOException  If the creation of the file fails.
     * @throws HippoFileException  If the creation of the file fails.
     */
    private static void grabFiles() throws IOException, HippoFileException {
        System.out.println("\n\n#Getting an instance to an existing HippoFile: \n");

        final HippoFileElement extraElement = new HippoFileElement("ExtraElement", "720blazeit");

        final HippoFileObject extraObject = new HippoFileObject("ExtraObject")
                .addElement(extraElement);

        final HippoFile grabFile = HippoFileService.getFile("/home/hippo/Desktop/Systems/HippoFile/bin/Test.hippo", Encrypter.standard())
                .addObject(extraObject)
                .encrypt();

        for(final HippoFileObject content : grabFile.getContent()){
            System.out.println(content.getContent() + "\n");
        }


        for(final Object value : grabFile.getObject("ExtraObject").getElement("ExtraElement").getValues()){
            System.out.println(value);
        }

        System.out.println("\nTEXT\n" + HippoFileService.convertToText(grabFile));

    }
}
