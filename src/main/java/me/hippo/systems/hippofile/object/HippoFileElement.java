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
public final class HippoFileElement {

    /**
     * The name of the {@link HippoFileElement}.
     */
    private final String name;

    /**
     * An {@code array} of {@link Object}s to store values.
     */
    private final Object[] values;

    /**
     * An {@link ArrayList} of {@link HippoFileElement}s to store all the {@link HippoFileElement}'s children.
     */
    private final ArrayList<HippoFileElement> children;

    /**
     * Creates a new {@link HippoFileElement} with the desired name and values.
     * @param name  The name.
     * @param values  The values.
     * @throws HippoFileException  If the creation fails.
     */
    public HippoFileElement(final String name, final Object... values) throws HippoFileException {
        this.name = name;
        this.values = values;
        children = new ArrayList<>();
        for(final Object value : values){
            if(value instanceof HippoFileElement){
                throw new HippoFileException("Error, you cannot have a 'HippoFileElement' as a value of a HippoFileElement, if you want to attach the element please add it as a child.\nmyElement.addChild(anotherElement);");
            }
        }
    }

    /**
     * Scans for child {@link HippoFileElement}s within the boundary of {@code bounds}.
     * <p>
     *     Also collects all the children inside of children.
     *     I.E. you can have as many children inside of children as you want and this will still collect it.
     * </p>
     * @param bounds  The boundary to scan.
     * @throws HippoFileException  If the creation of the new {@link HippoFileElement} fails.
     */
    public void scanChildElements(final String bounds) throws HippoFileException {
        final StringBuilder boundBuilder = new StringBuilder(bounds);
        boundBuilder.delete(0, 1).delete(boundBuilder.length() - 1, boundBuilder.length());

        final StringBuilder nameBuilder = new StringBuilder();
        int nameEnd = 0;
        for(final char nameChar : boundBuilder.toString().toCharArray()){
            if(nameChar == '[' || nameChar == '('){
                break;
            }
            nameBuilder.append(nameChar);
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
        children.add(hippoFileElement);
    }

    /**
     * Gets all the content inside of the {@link HippoFileElement}.
     * @return  The content.
     */
    public String getContent(){
        final StringBuilder content = new StringBuilder();
        content.append("(").append(name);
        for(final Object value : values){
            content.append("[").append(value).append("]");
        }
        for(final HippoFileElement children : children){
            content.append(children.getContent());
        }
        content.append(")");
        return content.toString().replace("[[", "[").replace("]]", "]");
    }

    /**
     * Gets a child {@link HippoFileElement} by its name.
     * @param child  The name of the child {@link HippoFileElement}.
     * @return  The child {@link HippoFileElement}.
     * @throws HippoFileException  If the child does not exist.
     */
    public HippoFileElement getChild(final String child) throws HippoFileException {
        for(final HippoFileElement childElement : children){
            if(childElement.name.equalsIgnoreCase(child)){
                return childElement;
            }
        }
        throw new HippoFileException("An exception was thrown whilst finding child element!\n\nDetails:\nElement Name: " + name + "\nChild Name: " + child + "\nFound: null\n\nMaybe the child element does not exist?");
    }

    /**
     * Adds a child {@link HippoFileElement} to the {@code children}.
     * @param child  The child to add.
     * @return  The element.
     */
    public HippoFileElement addChild(final HippoFileElement child){
        children.add(child);
        return this;
    }

    /**
     * Gets the name of the {@link HippoFileElement}.
     * @return  The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the values of the {@link HippoFileElement}.
     * @return  The values.
     */
    public Object[] getValues() {
        return values;
    }

    /**
     * Gets the {@link ArrayList} of child {@link HippoFileElement}s.
     * @return  The children.
     */
    public ArrayList<HippoFileElement> getChildren() {
        return children;
    }
}
