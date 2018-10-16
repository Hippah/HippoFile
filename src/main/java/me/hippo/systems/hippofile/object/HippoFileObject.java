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

package me.hippo.systems.hippofile.object;

import me.hippo.systems.hippofile.exception.HippoFileException;

import java.util.ArrayList;

/**
 * @author Hippo
 * @since 10/15/2018
 */
public final class HippoFileObject {

    /**
     * The name of the {@link HippoFileObject}.
     */
    private final String name;

    /**
     * An {@link ArrayList} of {@link HippoFileElement}s to store all the {@link HippoFileObject}'s elements.
     */
    private final ArrayList<HippoFileElement> elements;

    /**
     * Creates a new {@link HippoFileObject} with the desired name.
     * @param name  The name of the {@link HippoFileObject}.
     */
    public HippoFileObject(final String name) {
        this.name = name;
        this.elements = new ArrayList<>();
    }

    /**
     * Gets an {@link HippoFileElement} by it's name.
     * @param element  The element to get.
     * @return  The element.
     * @throws HippoFileException  If the element does not exist.
     */
    public HippoFileElement getElement(final String element) throws HippoFileException {
        for (final HippoFileElement hippoFileElement : elements) {
            if (hippoFileElement.getName().equalsIgnoreCase(element)) {
                return hippoFileElement;
            }
        }
        throw new HippoFileException("An exception was thrown whilst finding element!\n\nDetails:\nObject Name: " + name + "\nElement Name: " + element + "\nFound: null\n\nMaybe the element does not exist?");
    }

    /**
     * Adds an {@link HippoFileElement} to the {@code elements}.
     * @param element  The element to add.
     * @return  The hippo file object.
     */
    public HippoFileObject addElement(final HippoFileElement element){
        elements.add(element);
        return this;
    }

    /**
     * Scans an {@code array} of {@code char} for {@link HippoFileElement}s.
     * @param bounds  The {@code array} to scan.
     * @throws HippoFileException  If creating the new {@link HippoFileElement} fails.
     */
    public void scanElements(final char[] bounds) throws HippoFileException {
        final StringBuilder boundBuilder = new StringBuilder();
        for(final char character : bounds){
            boundBuilder.append(character);
        }
        boundBuilder.delete(0, 1).delete(boundBuilder.length() - 1, boundBuilder.length());

        final StringBuilder nameBuilder = new StringBuilder();
        int nameEnd = 0;
        for(final char character : boundBuilder.toString().toCharArray()){
            if(character == '[' || character == '('){
                break;
            }
            nameBuilder.append(character);
            nameEnd++;
        }
        boundBuilder.delete(0, nameEnd + 1);

        final ArrayList<String> values = new ArrayList<>();
        final StringBuilder valueBuilder = new StringBuilder();
        int childIndex = -1;
        for(int i = 0; i < boundBuilder.toString().length(); i++){
            final char value = boundBuilder.toString().charAt(i);
            if(value == ']'){
                values.add(valueBuilder.toString());
                valueBuilder.delete(0, valueBuilder.length());
            }else if(value == '('){
                childIndex = i;
                break;
            }else if(value != '['){
                valueBuilder.append(value);
            }
        }
        final HippoFileElement hippoFileElement = new HippoFileElement(nameBuilder.toString(), values);
        if(childIndex > -1){
            boundBuilder.delete(0, childIndex);
            hippoFileElement.scanChildElements(boundBuilder.toString());
        }

        elements.add(hippoFileElement);
    }

    /**
     * Gets the list of {@link HippoFileElement}s.
     * @return  The elements.
     */
    public ArrayList<HippoFileElement> getElements() {
        return elements;
    }

    /**
     * Gets all the content inside of the {@link HippoFileObject}.
     * @return  The content.
     */
    public String getContent(){
        final StringBuilder content = new StringBuilder();
        content.append(name + "{");
        for(final HippoFileElement hippoFileElement : elements){
            content.append(hippoFileElement.getContent());
        }
        content.append("}\n");
        return content.toString();
    }

    /**
     * Gets the {@link HippoFileObject}'s name.
     * @return  The name.
     */
    public String getName() {
        return name;
    }
}
