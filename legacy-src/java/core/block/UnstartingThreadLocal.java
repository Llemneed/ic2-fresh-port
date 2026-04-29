// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.block;

class UnstartingThreadLocal<T> extends ThreadLocal<T>
{
    @Override
    protected T initialValue() {
        throw new UnsupportedOperationException();
    }
}
