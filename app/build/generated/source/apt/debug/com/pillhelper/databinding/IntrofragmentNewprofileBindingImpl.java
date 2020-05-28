package com.pillhelper.databinding;
import com.pillhelper.R;
import com.pillhelper.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class IntrofragmentNewprofileBindingImpl extends IntrofragmentNewprofileBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.signup_input_layout_name, 1);
        sViewsWithIds.put(R.id.profileName, 2);
        sViewsWithIds.put(R.id.radioButtonMale, 3);
        sViewsWithIds.put(R.id.radioButtonFemale, 4);
        sViewsWithIds.put(R.id.radioButtonOtherGender, 5);
        sViewsWithIds.put(R.id.signup_input_layout_name2, 6);
        sViewsWithIds.put(R.id.profileBirthday, 7);
        sViewsWithIds.put(R.id.signup_input_layout_name3, 8);
        sViewsWithIds.put(R.id.profileSize, 9);
        sViewsWithIds.put(R.id.create_newprofil, 10);
    }
    // views
    @NonNull
    private final android.widget.ScrollView mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public IntrofragmentNewprofileBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 11, sIncludes, sViewsWithIds));
    }
    private IntrofragmentNewprofileBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.Button) bindings[10]
            , (android.widget.EditText) bindings[7]
            , (android.widget.EditText) bindings[2]
            , (android.widget.EditText) bindings[9]
            , (android.widget.RadioButton) bindings[4]
            , (android.widget.RadioButton) bindings[3]
            , (android.widget.RadioButton) bindings[5]
            , (com.google.android.material.textfield.TextInputLayout) bindings[1]
            , (com.google.android.material.textfield.TextInputLayout) bindings[6]
            , (com.google.android.material.textfield.TextInputLayout) bindings[8]
            );
        this.mboundView0 = (android.widget.ScrollView) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}