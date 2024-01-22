//$Header: /as2/de/mendelson/util/security/signature/SignatureDisplay.java 4     8/12/22 11:35 Heller $
package de.mendelson.util.security.signature;

import java.util.Objects;
import javax.swing.ImageIcon;

/**
 * Container superclass for the signature rendering
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public abstract class SignatureDisplay{

    private final Object wrappedValue;
    
    public SignatureDisplay( Object wrappedValue ){
        this.wrappedValue = wrappedValue;
    }
    
    
    public abstract ImageIcon getIcon();
    
    public abstract String getText();
    
    public Object getWrappedValue(){
        return( this.wrappedValue );
    }

/**
         * Overwrite the equal method of object
         *
         * @param anObject object ot compare
         */
        @Override
        public boolean equals(Object anObject) {
            if (anObject == this) {
                return (true);
            }
            if (anObject != null && anObject instanceof SignatureDisplay) {
                SignatureDisplay entry = (SignatureDisplay) anObject;
                return (entry.wrappedValue.equals( this.wrappedValue));
            }
            return (false);
        }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.wrappedValue);
        return hash;
    }
    
}
