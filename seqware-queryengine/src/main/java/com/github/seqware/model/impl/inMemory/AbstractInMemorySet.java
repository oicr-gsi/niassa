package com.github.seqware.model.impl.inMemory;

import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.Feature;
import com.github.seqware.model.interfaces.AbstractSet;
import com.github.seqware.model.Molecule;
import com.github.seqware.model.interfaces.Taggable;
import com.github.seqware.model.impl.MoleculeImpl;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An in-memory representation of a AbstractSet.
 *
 * @author dyuen
 */
public abstract class AbstractInMemorySet<S extends AbstractSet, T> extends MoleculeImpl<S> implements AbstractSet<S, T>, Taggable{
    
    protected Set<T> set = new HashSet<T>();
    
    protected AbstractInMemorySet(){
        super();
    }

    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

    @Override
    public S add(T tag) {
        set.add(tag);
        if (this.getManager() != null){
            this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);
        }
        return (S) this;
    }

    @Override
    public S add(Set<T> tags) {
        this.set.addAll(tags);
        if (this.getManager() != null){
            this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);  
        }
        return (S) this;
    }
    
    @Override
    public S add(T ... tags) {
        this.set.addAll(Arrays.asList(tags));
        if (this.getManager() != null){
            this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION); 
        }
        return (S) this;
    }
    
    @Override
    public S remove(T tag) {
        this.set.remove(tag);
        if (this.getManager() != null){
            this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);
        }
        return (S) this;
    }

    @Override
    public long getCount() {
        return set.size();
    }
}
