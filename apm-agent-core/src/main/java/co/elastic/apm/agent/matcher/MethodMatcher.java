/*
 * Licensed to Elasticsearch B.V. under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch B.V. licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package co.elastic.apm.agent.matcher;

import co.elastic.apm.agent.common.util.WildcardMatcher;
import org.stagemonitor.util.StringUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static co.elastic.apm.agent.matcher.AnnotationMatcher.annotationMatcher;
import static co.elastic.apm.agent.common.util.WildcardMatcher.caseSensitiveMatcher;

public class MethodMatcher {

    private static final String MODIFIER = "(?<modifier>public|private|protected|\\*)";
    private static final String ANNOTATION = "((?<annotation>@@?[a-zA-Z\\d_$.\\*]+)\\s+)?";
    private static final String CLASS_NAME = "(?<clazz>[a-zA-Z\\d_$.\\*]+)";
    private static final String METHOD_NAME = "(?<method>[a-zA-Z\\d_$\\*]+)";
    private static final String PARAM = "([a-zA-Z\\d_$.\\[\\]\\*]+)";
    private static final String PARAMS = PARAM + "(,\\s*" + PARAM + ")*";
    private static final Pattern METHOD_MATCHER_PATTERN = Pattern.compile("^(" + MODIFIER + "\\s+)?" + ANNOTATION + CLASS_NAME + "(#" + METHOD_NAME + "(?<params>\\((" + PARAMS + ")*\\))?)?$");

    private final String stringRepresentation;
    @Nullable
    private final Integer modifier;
    private final AnnotationMatcher annotationMatcher;
    private final WildcardMatcher classMatcher;
    private final WildcardMatcher methodMatcher;
    @Nullable
    private final List<WildcardMatcher> argumentMatchers;

    private MethodMatcher(String stringRepresentation, @Nullable Integer modifier, AnnotationMatcher annotationMatcher, WildcardMatcher classMatcher, WildcardMatcher methodMatcher, @Nullable List<WildcardMatcher> argumentMatchers) {
        this.stringRepresentation = stringRepresentation;
        this.modifier = modifier;
        this.annotationMatcher = annotationMatcher;
        this.classMatcher = classMatcher;
        this.methodMatcher = methodMatcher;
        this.argumentMatchers = argumentMatchers;
    }

    public static MethodMatcher of(String methodMatcher) {
        final Matcher matcher = METHOD_MATCHER_PATTERN.matcher(methodMatcher);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("'" + methodMatcher + "'" + " is not a valid method matcher");
        }

        final String modifier = matcher.group("modifier");
        final AnnotationMatcher annotationMatcher = matcher.group("annotation") != null ? annotationMatcher(matcher.group("annotation")) : AnnotationMatcher.matchAll();
        final WildcardMatcher clazz = caseSensitiveMatcher(matcher.group("clazz"));
        final WildcardMatcher method = matcher.group("method") != null ? caseSensitiveMatcher(matcher.group("method")) : WildcardMatcher.matchAll();
        final List<WildcardMatcher> args = getArgumentMatchers(matcher.group("params"));
        return new MethodMatcher(methodMatcher, getModifier(modifier), annotationMatcher, clazz, method, args);
    }

    @Nullable
    private static Integer getModifier(@Nullable String modifier) {
        if (modifier == null) {
            return null;
        }
        switch (modifier) {
            case "public":
                return Modifier.PUBLIC;
            case "private":
                return Modifier.PRIVATE;
            case "protected":
                return Modifier.PROTECTED;
            default:
                return null;
        }
    }

    @Nullable
    private static List<WildcardMatcher> getArgumentMatchers(@Nullable String arguments) {
        if (arguments == null) {
            return null;
        }
        // remove parenthesis
        arguments = arguments.substring(1, arguments.length() - 1);
        final String[] splitArguments = StringUtils.split(arguments, ',');
        List<WildcardMatcher> matchers = new ArrayList<>(splitArguments.length);
        for (String argument : splitArguments) {
            matchers.add(caseSensitiveMatcher(argument.trim()));
        }
        return matchers;
    }

    public AnnotationMatcher getAnnotationMatcher() {
        return annotationMatcher;
    }

    public WildcardMatcher getClassMatcher() {
        return classMatcher;
    }

    @Nullable
    public Integer getModifier() {
        return modifier;
    }

    public WildcardMatcher getMethodMatcher() {
        return methodMatcher;
    }

    @Nullable
    public List<WildcardMatcher> getArgumentMatchers() {
        return argumentMatchers;
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }
}
