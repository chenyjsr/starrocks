// This file is licensed under the Elastic License 2.0. Copyright 2021-present, StarRocks Limited.

package com.starrocks.sql.optimizer.operator.scalar;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.starrocks.catalog.Type;
import com.starrocks.sql.optimizer.base.ColumnRefSet;

import java.util.List;
import java.util.stream.Collectors;

import static com.starrocks.sql.optimizer.operator.OperatorType.ARRAY_ELEMENT;

public class ArrayElementOperator extends ScalarOperator {
    private final List<ScalarOperator> arguments = Lists.newArrayList();

    public ArrayElementOperator(Type type, ScalarOperator arrayOperator, ScalarOperator subscriptOperator) {
        super(ARRAY_ELEMENT, type);
        this.arguments.add(arrayOperator);
        this.arguments.add(subscriptOperator);
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public List<ScalarOperator> getChildren() {
        return arguments;
    }

    @Override
    public ScalarOperator getChild(int index) {
        return arguments.get(index);
    }

    @Override
    public void setChild(int index, ScalarOperator child) {
        arguments.set(index, child);
    }

    @Override
    public String toString() {
        return arguments.stream().map(ScalarOperator::toString).collect(Collectors.joining(","));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArrayElementOperator that = (ArrayElementOperator) o;
        return Objects.equal(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(arguments);
    }

    public ColumnRefSet getUsedColumns() {
        ColumnRefSet used = new ColumnRefSet();
        for (ScalarOperator child : arguments) {
            used.union(child.getUsedColumns());
        }
        return used;
    }

    @Override
    public <R, C> R accept(ScalarOperatorVisitor<R, C> visitor, C context) {
        return visitor.visitArrayElement(this, context);
    }
}
