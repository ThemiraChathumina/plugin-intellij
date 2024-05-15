/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.ballerina.plugins.idea.spellchecker;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.spellchecker.inspections.CommentSplitter;
import com.intellij.spellchecker.tokenizer.TokenConsumer;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import org.jetbrains.annotations.NotNull;

/**
 * Used to tokenize words in comments.
 *
 * @since 2.0.0
 */
public class BallerinaCommentTokenizer extends Tokenizer<PsiComment> {

    @Override
    public void tokenize(@NotNull PsiComment psiComment, @NotNull TokenConsumer tokenConsumer) {
        int startIndex = 0;
        for (char c : psiComment.textToCharArray()) {
            if (c == '/' || Character.isWhitespace(c)) {
                startIndex++;
            } else {
                break;
            }
        }
        tokenConsumer.consumeToken(psiComment, psiComment.getText(), false, 0,
                TextRange.create(startIndex, psiComment.getTextLength()),
                CommentSplitter.getInstance());
    }
}
