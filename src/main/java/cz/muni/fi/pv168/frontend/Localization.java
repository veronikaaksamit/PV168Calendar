/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.frontend;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Dadka
 */
public class Localization {
    
    private static Locale currentLocale = Locale.US;   
    private static ResourceBundle rbTexts;

    public static ResourceBundle getRbTexts() {
        return rbTexts;
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static void setCurrentLocale(Locale currentLocale) {
        Localization.currentLocale = currentLocale;
        rbTexts = ResourceBundle.getBundle("texts", currentLocale);
    }
    
}
