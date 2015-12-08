package fr.manumehdi.gazouille.data.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by mehdi on 21/11/2015.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {

}