/*
 * Copyright (c) 2024-2025 balugaq
 *
 * This file is part of JustEnoughGuide, available under MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The author's name (balugaq or 大香蕉) and project name (JustEnoughGuide or JEG) shall not be
 *   removed or altered from any source distribution or documentation.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.balugaq.jeg.utils.formatter;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 1.6
 */
public class RecipeDisplayFormat extends Format {
    public static @NotNull List<Integer> fenceShuffle(@NotNull List<Integer> list) {
        int size = list.size();
        int splitPoint = (size + 1) / 2;

        List<Integer> firstHalf = new ArrayList<>(list.subList(0, splitPoint));
        List<Integer> secondHalf = new ArrayList<>(list.subList(splitPoint, size));

        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < secondHalf.size(); i++) {
            result.add(firstHalf.get(i));
            result.add(secondHalf.get(i));
        }

        if (firstHalf.size() > secondHalf.size()) {
            result.add(firstHalf.get(firstHalf.size() - 1));
        }

        return result;
    }

    @Override
    public void loadMapping() {
        loadMapping(JustEnoughGuide.getConfigManager().getRecipeDisplayFormat());
    }
}
