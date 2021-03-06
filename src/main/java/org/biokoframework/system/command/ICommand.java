/*
 * Copyright (c) 2014
 *	Mikol Faro			<mikol.faro@gmail.com>
 *	Simone Mangano		<simone.mangano@ieee.org>
 *	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package org.biokoframework.system.command;

import org.biokoframework.system.context.Context;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;

public interface ICommand {

	public Fields execute(Fields input) throws CommandException, ValidationException;
	
	@Deprecated
	void setContext(Context context);
	@Deprecated
	Fields componingInputKeys();
	@Deprecated
	Fields componingOutputKeys();

    /**
     * This method simply acts a friendly reminder not to implement ICommand directly and
     * instead extend AbstractMacher. It's easy to ignore JavaDoc, but a bit harder to
     * ignore compile errors.
     *
     * @see ICommand for reasons why.
     * @see AbstractCommand
     * @deprecated to make
     */
    @Deprecated
    void _dont_implement_ICommand___instead_extend_AbstractCommand_();
}
