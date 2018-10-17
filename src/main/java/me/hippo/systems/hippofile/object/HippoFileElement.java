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
     * Creates a new {@link HippoFileElement} with the desired name and values.
     * @param name  The name.
     * @param values  The values.
     * @throws HippoFileException  If the creation fails.
     */
    public HippoFileElement(final String name, final Object... values) throws HippoFileException {
        this.name = name;
        this.values = values;
        for(final Object value : values){
            if(value instanceof HippoFileElement){
                throw new HippoFileException("Error, you cannot have a 'HippoFileElement' as a value of a HippoFileElement, if you want to attach the element please add it as a child.\nmyElement.addChild(anotherElement);");
            }
        }
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

        content.append(")");
        return content.toString().replace("[[", "[").replace("]]", "]");
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
}
