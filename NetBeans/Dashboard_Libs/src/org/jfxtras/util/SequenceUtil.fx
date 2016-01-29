/*
 * Copyright (c) 2008-2009, JFXtras Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of JFXtras nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jfxtras.util;

import java.lang.Object;
import java.util.Comparator;
import javafx.util.Sequences;

import javafx.util.Math;

/**
 * Library of static Sequence Utilities that can be used to perform common operations,
 * such as adding or concatenating.  Also provides a generic fold operation
 * (ala. Haskell or ML) that can be used to reduce a generic list of Objects.
 *
 * Currently there is only type support for Numbers, Integers, and Objects.
 *
 * @profile common
 *
 * @author Stephen Chin
 * @author Keith Combs
 */
public abstract class SequenceUtil {} // placeholder to generate javadoc

/**
 * Adds up all the elements of a sequence returning the total.  This function
 * is typed for Number sequences.
 *
 * @param seq A sequence of Numbers
 * @return The sum of the sequence
 */
public function sum(seq:Number[]):Number {
    return fold(0.0, seq, function(a:Number, b:Number) {
        a + b
    });
}

/**
 * Adds up all the elements of a sequence returning the total.  This function
 * is typed for Integer sequences.
 *
 * @param seq A sequence of Integers
 * @return The sum of the sequence
 */
public function sum(seq:Integer[]):Integer {
    return fold(0, seq, function(a:Integer, b:Integer) {
        a + b
    });
}

/**
 * Calculates the mathematical average of a sequence of numbers.  This function
 * is typed for Number sequences.
 *
 * @param seq A sequence of Numbers
 * @return The sum of the sequence
 */
public function avg(seq:Number[]):Number {
    return fold(0.0, seq, function(a : Number, b : Number) {
        a + b
    }) / sizeof seq;
}

/**
 * Calculates the mathematical average of a sequence of numbers.  This function
 * is typed for Integer sequences.
 *
 * @param seq A sequence of Integers
 * @return The sum of the sequence
 */
public function avg(seq:Integer[]):Integer {
    return fold(0, seq, function(a : Integer, b : Integer) {
        a + b
    }) / sizeof seq;
}

/**
 * Concatenates all the elements of a String sequence, returning a single merged
 * String.
 *
 * @param seq A sequence of Strings
 * @return A single concatenated value
 */
public function concat(seq:String[]):String {
    return 
    fold("", seq, function(a, b) {"{a}{b}"
    }) as String;
}

/**
 * Joins all the elements of a String sequence using the specified delimiter
 * String between elements.  Thre result is a single merged String with one
 * less delimiter than the size of the input sequence.
 *
 * @param seq A sequence of Strings
 * @return A single value joined by the delimiter
 */
public function join(seq:String[], delimiter:String):String {
    return if (seq.size() == 0) "" else "{seq[0]}{fold("", seq[1..], function(a, b) {"{a}{delimiter}{b}"})}";
}

/**
 * Generic fold function typed for Number sequences.  This function takes an
 * identity Number, a sequence of Numbers, and a reduce function, and returns
 * a single value.
 * <p>
 * This function performs what is known as a left fold in functional languages.
 * The basic algorithm is that the ident value and first element of the list
 * are passed in to the given reduce function, resulting in a single value.
 * That value is then passed together with the second element of the list back
 * into the reduce function resulting in a single value again.  This process
 * repeats until there are no elements left in the list and a single return
 * value results.
 * <p>
 * For a more thorough explanation of how the fold operator works, please
 * refer to the following article:
 * <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">
 * http://en.wikipedia.org/wiki/Fold_(higher-order_function)</a>
 *
 * @param ident The identity value
 * @param seq A sequence of Numbers
 * @param func A reduce function
 * @return A single value that is the result of applying the reduce function
 *         to all the elements in the sequence.
 */
public function fold(ident:Number, seq:Number[], func:function(num1:Number, num2:Number):Number):Number {
    var accum = ident;
    for (elem in seq) {
        accum = func(accum, elem);
    }
    return accum;
}

/**
 * Generic fold function typed for Integer sequences.  This function takes an
 * identity Integer, a sequence of Integers, and a reduce function, and returns
 * a single value.
 * <p>
 * This function performs what is known as a left fold in functional languages.
 * The basic algorithm is that the ident value and first element of the list
 * are passed in to the given reduce function, resulting in a single value.
 * That value is then passed together with the second element of the list back
 * into the reduce function resulting in a single value again.  This process
 * repeats until there are no elements left in the list and a single return
 * value results.
 * <p>
 * For a more thorough explanation of how the fold operator works, please
 * refer to the following article:
 * <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">
 * http://en.wikipedia.org/wiki/Fold_(higher-order_function)</a>
 *
 * @param ident The identity value
 * @param seq A sequence of Integers
 * @param func A reduce function
 * @return A single value that is the result of applying the reduce function
 *         to all the elements in the sequence.
 */
public function fold(ident:Integer, seq:Integer[], func:function(int1:Integer, int2:Integer):Integer):Integer {
    var accum = ident;
    for (elem in seq) {
        accum = func(accum, elem);
    }
    return accum;
}

/**
 * Generic fold function typed for Object sequences.  This function takes an
 * identity Object, a sequence of Objects, and a reduce function, and returns
 * a single value.
 * <p>
 * This function performs what is known as a left fold in functional languages.
 * The basic algorithm is that the ident value and first element of the list
 * are passed in to the given reduce function, resulting in a single value.
 * That value is then passed together with the second element of the list back
 * into the reduce function resulting in a single value again.  This process
 * repeats until there are no elements left in the list and a single return
 * value results.
 * <p>
 * For a more thorough explanation of how the fold operator works, please
 * refer to the following article:
 * <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">
 * http://en.wikipedia.org/wiki/Fold_(higher-order_function)</a>
 *
 * @param ident The identity value
 * @param seq A sequence of Objects
 * @param func A reduce function
 * @return A single value that is the result of applying the reduce function
 *         to all the elements in the sequence.
 */
public function fold(ident:Object, seq:Object[], func:function(obj1:Object, obj2:Object):Object):Object {
    var accum = ident;
    for (elem in seq) {
        accum = func(accum, elem);
    }
    return accum;
}

/**
 * A character sequence generator.  This function generates a sequence of one-character strings
 * starting from the first character of the <code>start</code> string and ending with the first
 * character of the <code>end</code> string.
 *
 * @param start The character that starts the sequence.
 * @param end The character that ends the sequence.
 * @return A sequence of one-character strings.
 */
public function characterSequence(start:String, end:String): String[] {
    var startIdx = start.charAt(0) as Integer;
    var endIdx = end.charAt(0) as Integer;
    var stepSize = if (startIdx < endIdx) 1 else -1;

    return for (x in [startIdx..endIdx step stepSize]) { 
        (x as Character).toString()
    }
}

def identitityComparator = Comparator {
    override function compare(node1:Object, node2:Object) {
        return node1.hashCode().compareTo(node2.hashCode());
    }
}

/**
 * Reorders the updated sequence to match the order of the original sequence.
 * Specifically, the following constraints are obeyed:
 * <ul>
 * <li>The order of nodes that exist in both sequences are guaranteed to be the same
 * <li>Elements that exist in the source, but not the destination will get added to the end
 * <li>Elements that exist in the destination but not the source will get deleted
 * </ul>
 * <p>
 * The resulting sequence will be returned from this function (updates are not
 * made in place).
 * <p>
 * If the same element is in either sequence twice, the duplicates will be
 * preserved as long as there is at least one instance of the element in both
 * the original and updates sequences.
 *
 * @param source The sequence that the destination will be updated from
 * @param destination The sequence to update in place wrapped with a SequenceHolder
 */
public function stableUpdate(original:Object[], updated:Object[]):Object[] {
    var destination:Object[];
    var sortedUpdated = Sequences.sort(updated, identitityComparator) as Object[];
    for (node in original) {
        var location = Sequences.binarySearch(sortedUpdated, node, identitityComparator);
        if (location >= 0) {
            insert node into destination;
        }
    }
    var sortedOriginal = Sequences.sort(original, identitityComparator) as Object[];
    for (node in updated) {
        var location = Sequences.binarySearch(sortedOriginal, node, identitityComparator);
        if (location < 0) {
            insert node into destination;
        }
    }
    return destination;
}

/**
 * Utility function for moving a window across a sequence where elements that move out of the window need
 * to be removed and elements that move in to the window need to be added.
 * <p>
 * All start and end parameters are inclusive.
 */
public function updateWindow(seq:Object[], oldWindowStart:Integer, oldWindowEnd:Integer, newWindowStart:Integer, newWindowEnd:Integer, removeFunction:function(:Object, :Integer):Void, addFunction:function(:Object, :Integer):Void) {
    for (e in seq[oldWindowStart..Math.min(newWindowStart-1, oldWindowEnd)]) {
        removeFunction(e, indexof e + oldWindowStart);
    }
    var fromIndex = Math.max(newWindowEnd+1, oldWindowStart);
    for (e in seq[fromIndex..oldWindowEnd]) {
        removeFunction(e, indexof e + fromIndex);
    }
    fromIndex = Math.max(oldWindowEnd+1, newWindowStart);
    for (e in seq[fromIndex..newWindowEnd]) {
        addFunction(e, indexof e + fromIndex);
    }
    for (e in seq[newWindowStart..Math.min(oldWindowStart-1, newWindowEnd)]) {
        addFunction(e, indexof e + newWindowStart);
    }
}
