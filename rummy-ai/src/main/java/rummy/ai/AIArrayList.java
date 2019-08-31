package rummy.ai;

import java.util.Arrays;
import java.util.List;

public class AIArrayList<E> { // E means the (generic) type of objects in this list (it is specified so you can create AIArrayLists containing only specific type objects)
    
    private Object[] array;
    private int index; // Array index where the next object will be added to
    
    public AIArrayList() {
        this.array = new Object[10]; // Note: Cannot use generic E as array type
        this.index = 0;
    }
    
    public void add(E addition) {
        if (this.index == this.array.length) { // If the array is full, increase its size
            increaseSize();
        }
        
        this.array[this.index] = addition;
        this.index++;
    }
    
    @SuppressWarnings("unchecked")
    public E get(int i) {
        if (i >= this.index || i < 0) {
            throw new IndexOutOfBoundsException("Index: " + i + ", size: " + this.index);
        }
        return (E) this.array[i];
    }
    
    public boolean contains(E element) {
        for (int i = 0; i < this.size(); i++) {
            if (this.array[i].equals(element)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsAll(List<E> another) {
        if (another.size() > this.size()) {
            return false;
        }
        
        for (int i = 0; i < another.size(); i++) {
            boolean found = false;
            
            for (int j = 0; j < this.size(); j++) {
                if (another.get(i).equals(this.get(j))) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean containsAll(AIArrayList<E> another) {
        if (another.size() > this.size()) {
            return false;
        }
        
        for (int i = 0; i < another.size(); i++) {
            boolean found = false;
            
            for (int j = 0; j < this.size(); j++) {
                if (another.get(i).equals(this.get(j))) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                return false;
            }
        }
        
        return true;
    }
    
    public int size() {
        return this.index;
    }
    
    public boolean isEmpty() {
        return this.index == 0;
    }
    
    private void increaseSize() {
        Object[] newArray = new Object[this.array.length * 2];
        for (int i = 0; i < this.array.length; i++) {
            newArray[i] = this.array[i];
        }
        
        this.array = newArray;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Arrays.deepHashCode(this.array);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AIArrayList<?> other = (AIArrayList<?>) obj;
        
        return Arrays.deepEquals(this.array, other.array);
    }
    
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("[");
        
        for (int i = 0; i < this.index - 1; i++) { // Skipping the last loop so the sequence won't end with a comma
            sb.append(this.array[i].toString());
            sb.append(", ");
        }
        
        sb.append(this.array[this.index - 1]);
        sb.append("]");
        
        return sb.toString();
    }

}
