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

package it.bioko.system.repository.core.query;

import it.bioko.utils.domain.DomainEntity;

public interface Constraint<DE extends DomainEntity> {

	public Constraint<DE> setFieldName(String fieldName);

	public Query<DE> placeholder(String placeholder);
	
	public Constraint<DE> isEqual();
	public Query<DE> isEqual(String value);
	public Constraint<DE> isNotEqual();
	public Query<DE> isNotEqual(String value);

	public Constraint<DE> like();
	public Query<DE> like(String value);
	public Constraint<DE> notLike();
	public Query<DE> notLike(String value);

	public Constraint<DE> not();

	public Query<DE> ilike(String value);
	public Constraint<DE> ilike();

	public Query<DE> slike(String value);
	public Constraint<DE> slike();

	public Query<DE> lt(String string);
	public Constraint<DE> lt();

	public Query<DE> lte(String string);
	public Constraint<DE> lte();
	
	public Query<DE> gt(String string);
	public Constraint<DE> gt();

	public Query<DE> gte(String string);
	public Constraint<DE> gte();
	
}
