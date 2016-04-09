package com.example.androidSacco;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private EditText inputFullName;
    private EditText inputGender;
    private EditText inputDOB;
    private EditText inputMaritalStatus;
    private EditText inputAddress;
    private EditText inputPhoneNo;
    private EditText inputEmail;
    private EditText inputOccupation;
    private EditText inputMemberSince;
    private EditText inputPassword;
    private EditText inputMemberNo;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputGender=(EditText) findViewById(R.id.gender);
        inputDOB=(EditText)findViewById(R.id.DOB);
        inputMaritalStatus=(EditText) findViewById(R.id.maritalStatus);
        inputAddress=(EditText)findViewById(R.id.address);
        inputPhoneNo=(EditText) findViewById(R.id.phoneNo);
        inputEmail = (EditText) findViewById(R.id.email);
        inputOccupation=(EditText) findViewById(R.id.occupation);
        inputMemberNo = (EditText) findViewById(R.id.memberNo);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String gender=inputGender.getText().toString().trim();
                String dOB=inputDOB.getText().toString().trim();
                String maritalStatus=inputMaritalStatus.getText().toString().trim();
                String address=inputAddress.getText().toString().trim();
                String phoneNo=inputPhoneNo.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String occupation=inputOccupation.getText().toString().trim();
                String memberNo=inputMemberNo.getText().toString().trim();

                if (!memberNo.isEmpty() && !name.isEmpty() && !email.isEmpty() && !address.isEmpty()&& !phoneNo.isEmpty()&& !occupation.isEmpty()) {
                    registerUser(memberNo,name,email,address,phoneNo,occupation);
                } else {
                    Toast.makeText(getApplicationContext(),"Please fill all compulsory fields!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String memberNumber,final String name,final String email,final String address,final String phoneNo,final String occupation) throws NullPointerException
    {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,AppConfig.URL_REGISTER, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Registering Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        /*// User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");

                        // Inserting row in users table
                       // db.addUser(name, email, uid, created_at);*/

                        Toast.makeText(getApplicationContext(), "Member successfully registered", Toast.LENGTH_LONG).show();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("memberNo",memberNumber);
                params.put("name", name);
                params.put("address", address);
                params.put("occupation",occupation);
                params.put("email", email);
                params.put("phoneNo",phoneNo);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}