package com.easyfitness.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.google.android.material.textfield.TextInputLayout;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class IntrofragmentNewprofileBinding extends ViewDataBinding {
  @NonNull
  public final Button createNewprofil;

  @NonNull
  public final EditText profileBirthday;

  @NonNull
  public final EditText profileName;

  @NonNull
  public final EditText profileSize;

  @NonNull
  public final RadioButton radioButtonFemale;

  @NonNull
  public final RadioButton radioButtonMale;

  @NonNull
  public final RadioButton radioButtonOtherGender;

  @NonNull
  public final TextInputLayout signupInputLayoutName;

  @NonNull
  public final TextInputLayout signupInputLayoutName2;

  @NonNull
  public final TextInputLayout signupInputLayoutName3;

  protected IntrofragmentNewprofileBinding(Object _bindingComponent, View _root,
      int _localFieldCount, Button createNewprofil, EditText profileBirthday, EditText profileName,
      EditText profileSize, RadioButton radioButtonFemale, RadioButton radioButtonMale,
      RadioButton radioButtonOtherGender, TextInputLayout signupInputLayoutName,
      TextInputLayout signupInputLayoutName2, TextInputLayout signupInputLayoutName3) {
    super(_bindingComponent, _root, _localFieldCount);
    this.createNewprofil = createNewprofil;
    this.profileBirthday = profileBirthday;
    this.profileName = profileName;
    this.profileSize = profileSize;
    this.radioButtonFemale = radioButtonFemale;
    this.radioButtonMale = radioButtonMale;
    this.radioButtonOtherGender = radioButtonOtherGender;
    this.signupInputLayoutName = signupInputLayoutName;
    this.signupInputLayoutName2 = signupInputLayoutName2;
    this.signupInputLayoutName3 = signupInputLayoutName3;
  }

  @NonNull
  public static IntrofragmentNewprofileBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.introfragment_newprofile, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static IntrofragmentNewprofileBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<IntrofragmentNewprofileBinding>inflateInternal(inflater, com.easyfitness.R.layout.introfragment_newprofile, root, attachToRoot, component);
  }

  @NonNull
  public static IntrofragmentNewprofileBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.introfragment_newprofile, null, false, component)
   */
  @NonNull
  @Deprecated
  public static IntrofragmentNewprofileBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<IntrofragmentNewprofileBinding>inflateInternal(inflater, com.easyfitness.R.layout.introfragment_newprofile, null, false, component);
  }

  public static IntrofragmentNewprofileBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.bind(view, component)
   */
  @Deprecated
  public static IntrofragmentNewprofileBinding bind(@NonNull View view,
      @Nullable Object component) {
    return (IntrofragmentNewprofileBinding)bind(component, view, com.easyfitness.R.layout.introfragment_newprofile);
  }
}
