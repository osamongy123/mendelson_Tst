//$Header: /as2/de/mendelson/util/security/keylength/KeyLengthDisplay.java 1     8/12/22 11:35 Heller $
package de.mendelson.util.security.keylength;

import de.mendelson.util.MendelsonMultiResolutionImage;
import java.util.Objects;
import javax.swing.ImageIcon;

/**
 * Container superclass for the key length rendering
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class KeyLengthDisplay{

    public final static MendelsonMultiResolutionImage IMAGE_KEYLENGTH_STRONG
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/keylength/keylength_strong.svg",
                    ListCellRendererKeyLength.IMAGE_HEIGHT);
    public final static MendelsonMultiResolutionImage IMAGE_KEYLENGTH_WEAK
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/keylength/keylength_weak.svg",
                    ListCellRendererKeyLength.IMAGE_HEIGHT);
    public final static MendelsonMultiResolutionImage IMAGE_KEYLENGTH_BROKEN
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/keylength/keylength_broken.svg",
                    ListCellRendererKeyLength.IMAGE_HEIGHT);
    
    private final String wrappedValue;
    
    public KeyLengthDisplay( String wrappedValue ){
        this.wrappedValue = wrappedValue;
    }
    
    
    public ImageIcon getIcon(){
        if( this.wrappedValue.equals("1024")){
            return( new ImageIcon(IMAGE_KEYLENGTH_WEAK.toMinResolution(
                    ListCellRendererKeyLength.IMAGE_HEIGHT)));
        }
        return( new ImageIcon(IMAGE_KEYLENGTH_STRONG.toMinResolution(
                ListCellRendererKeyLength.IMAGE_HEIGHT)));
    }
    
    public String getText(){
        return( this.wrappedValue);
    }
    
    public String getWrappedValue(){
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
            if (anObject != null && anObject instanceof KeyLengthDisplay) {
                KeyLengthDisplay entry = (KeyLengthDisplay) anObject;
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
