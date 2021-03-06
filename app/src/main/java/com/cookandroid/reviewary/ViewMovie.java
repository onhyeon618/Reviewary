package com.cookandroid.reviewary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.cookandroid.reviewary.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static com.cookandroid.reviewary.AppConstants.REQ_PHOTO_CAPTURE;

public class ViewMovie extends Fragment {
    private static final String TAG = "ViewMovie";

    String mCurrentPhotoPath;
    Uri imageUri;

    Context context;

    EditText etName, etDirector, etActor, etImpressive, etReview, etDate, etPlace;
    Date currentDate;
    int iYear, iMonth, iDay;
    Spinner spGenre;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;

    int MovieId = 0;

    RatingBar rb;
    TextView tv;

    // 이미지
    ImageButton imageButton;

    boolean isPhotoCaptured;
    boolean isPhotoFileSaved;
    boolean isPhotoCanceled;

    int selectedPhotoMenu;

    Bitmap resultPhotoBitmap;
    Bitmap rotatedBitmap = null;

    Button saveBtn, deleteBtn, cancelBtn;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(context != null) {
            context = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_view_movie, container, false);

        final ViewItemActivity activity = (ViewItemActivity) getActivity();

        initUI(rootView);

        MovieId = activity.getMyData();

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDateTextClicked(rootView);
            }
        });

        applyItem();


        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.isContextChanged = true;
            }
        });

        etDirector.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.isContextChanged = true;
            }
        });

        etActor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.isContextChanged = true;
            }
        });

        etImpressive.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.isContextChanged = true;
            }
        });

        etReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.isContextChanged = true;
            }
        });

        etDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.isContextChanged = true;
            }
        });

        etPlace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.isContextChanged = true;
            }
        });

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                activity.isContextChanged = true;
                if(rating <= 0.5f) {
                    ratingBar.setRating(0.5f);
                    tv.setText("0.5");
                }
                else
                    tv.setText(String.valueOf(rating));
            }
        });

        return rootView;
    }


    private void initUI(@NonNull final ViewGroup rootView) {

        imageButton = rootView.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPhotoCaptured || isPhotoFileSaved) {
                    showDialog(AppConstants.CONTENT_PHOTO_EX);
                } else {
                    showDialog(AppConstants.CONTENT_PHOTO);
                }
            }
        });

        etName = rootView.findViewById(R.id.etName);
        etDirector = rootView.findViewById(R.id.etDirector);
        etActor = rootView.findViewById(R.id.etActor);
        etPlace = rootView.findViewById(R.id.etPlace);
        spGenre = rootView.findViewById(R.id.spGenre);

        // Rating Bar
        rb = (RatingBar) rootView.findViewById(R.id.ratingBar);
        tv = (TextView) rootView.findViewById(R.id.tvRatingNum);

        // 독서 완료일
        etDate = (EditText) rootView.findViewById(R.id.etDate);
        //      getDateToday();


        // 장르 Spinner
        arrayList = new ArrayList<>();
        arrayList.add("로맨스");
        arrayList.add("추리");
        arrayList.add("스릴러");
        arrayList.add("가족");
        arrayList.add("드라마");
        arrayList.add("공포");
        arrayList.add("범죄");
        arrayList.add("다큐멘터리");
        arrayList.add("역사");
        arrayList.add("기타");

        arrayAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                arrayList);

        spGenre = (Spinner) rootView.findViewById(R.id.spGenre);
        spGenre.setAdapter(arrayAdapter);
        spGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        etImpressive = rootView.findViewById(R.id.etImpressive);
        etReview = rootView.findViewById(R.id.etReview);

        // 저장 버튼
        saveBtn = (Button) rootView.findViewById(R.id.btnSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.putExtra("state", "clear");
//                getActivity().setResult(RESULT_OK, intent);

                showSaveMsg();
            }
        });

        // 삭제 버튼
        deleteBtn = (Button) rootView.findViewById(R.id.btnDelete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.putExtra("state", "clear");
//                getActivity().setResult(RESULT_OK, intent);

                showDeleteMsg();
            }
        });

        // 취소 버튼
        cancelBtn = (Button) rootView.findViewById(R.id.btnCancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEndMsg();
            }
        });
    }



    private void setImage(String imagePath, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        rotatedBitmap = BitmapFactory.decodeFile(imagePath, options);

        imageButton.setImageBitmap(rotatedBitmap);
    }

    public void applyItem() {
        AppConstants.println("applyItem called.");

        String sql = "select _id, IMAGE, NAME, DIRECTOR, ACTOR, GENRE, RATING, DATE, PLACE, IMPRESSIVE_SENTENCE, REVIEW from " + MovieDataBase.TABLE_NOTE + " where " +
                "   _id = " + MovieId;

        MovieDataBase database = MovieDataBase.getInstance(context);
        Cursor outCursor = database.rawQuery(sql);

        outCursor.moveToNext();

        int _id = outCursor.getInt(0);
        String imagePath = outCursor.getString(1);
        String name = outCursor.getString(2);
        String director = outCursor.getString(3);
        String actor = outCursor.getString(4);
        String genre = outCursor.getString(5);
        String rating = outCursor.getString(6);
        String readDate = outCursor.getString(7);
        String place = outCursor.getString(8);
        String impressiveSentence = outCursor.getString(9);
        String review = outCursor.getString(10);

        etName.setText(name);
        etDirector.setText(director);
        etActor.setText(actor);
        etDate.setText(readDate);
        etImpressive.setText(impressiveSentence);
        etReview.setText(review);
        etPlace.setText(place);
        tv.setText(rating);
        rb.setRating(Float.parseFloat(rating));

        spGenre.setSelection(arrayList.indexOf(genre));

        if (imagePath == null || imagePath.equals("")) {
            isPhotoFileSaved = false;
            imageButton.setImageResource(R.drawable.noimagefound);
        } else {
            isPhotoFileSaved = true;
            setImage(imagePath, 1);
        }
    }


    private void setChangedTrue() {
        final ViewItemActivity activity = (ViewItemActivity) getActivity();
        activity.isContextChanged = true;
    }


    public void showDialog(int id) {
        AlertDialog.Builder builder = null;

        switch(id) {

            case AppConstants.CONTENT_PHOTO:
                builder = new AlertDialog.Builder(context);

                builder.setTitle("사진 메뉴 선택");
                builder.setSingleChoiceItems(R.array.array_photo, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        selectedPhotoMenu = whichButton;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(selectedPhotoMenu == 0 ) {
                            showPhotoCaptureActivity();
                        } else if(selectedPhotoMenu == 1) {
                            showPhotoSelectionActivity();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                break;

            case AppConstants.CONTENT_PHOTO_EX:
                builder = new AlertDialog.Builder(context);

                builder.setTitle("사진 메뉴 선택");
                builder.setSingleChoiceItems(R.array.array_photo_ex, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        selectedPhotoMenu = whichButton;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(selectedPhotoMenu == 0) {
                            showPhotoCaptureActivity();
                        } else if(selectedPhotoMenu == 1) {
                            showPhotoSelectionActivity();
                        } else if(selectedPhotoMenu == 2) {
                            isPhotoCanceled = true;
                            isPhotoCaptured = false;
                            setChangedTrue();

                            imageButton.setImageResource(R.drawable.noimagefound);
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                break;

            default:
                break;
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showPhotoCaptureActivity() {
        String state = Environment.getExternalStorageState();
        // 외장 메모리 검사
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.e("captureCamera Error", ex.toString());
                }
                if (photoFile != null) {
                    // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함

                    Uri providerURI = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName(), photoFile);
                    imageUri = providerURI;

                    // 인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);

                    startActivityForResult(takePictureIntent, REQ_PHOTO_CAPTURE);
                }
            }
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "Reviewary");

        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    private void galleryAddPic(){
        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
        // Toast.makeText(getActivity(), "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }


    public void showPhotoSelectionActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, AppConstants.REQ_PHOTO_SELECTION);
    }


    /**
     * 다른 액티비티로부터의 응답 처리
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case AppConstants.REQ_PHOTO_CAPTURE:  // 사진 찍는 경우
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Log.d(TAG, "onActivityResult() for REQ_PHOTO_CAPTURE.");
                        Log.d(TAG, "resultCode : " + resultCode);

                        galleryAddPic();

                        resultPhotoBitmap = decodeSampledBitmapFromResource(new File(mCurrentPhotoPath), imageButton.getWidth(), imageButton.getHeight());

                        ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        switch(orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(resultPhotoBitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(resultPhotoBitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(resultPhotoBitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = resultPhotoBitmap;
                        }

                        imageButton.setImageBitmap(rotatedBitmap);

                        isPhotoCaptured = true;
                        setChangedTrue();
                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                }
                break;

            case AppConstants.REQ_PHOTO_SELECTION:  // 사진을 앨범에서 선택하는 경우
                try {
                    Log.d(TAG, "onActivityResult() for REQ_PHOTO_SELECTION.");

                    Uri selectedImage = intent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    resultPhotoBitmap = decodeSampledBitmapFromResource(new File(filePath), imageButton.getWidth(), imageButton.getHeight());

                    ExifInterface ei = new ExifInterface(filePath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(resultPhotoBitmap, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(resultPhotoBitmap, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(resultPhotoBitmap, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = resultPhotoBitmap;
                    }

                    imageButton.setImageBitmap(rotatedBitmap);

                    isPhotoCaptured = true;
                    setChangedTrue();
                } catch (Exception e) {
                    Log.e("REQUEST_PHOTO_SELECTION", e.toString());
                }
                break;
        }
    }


    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static Bitmap decodeSampledBitmapFromResource(File res, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(res.getAbsolutePath(),options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(res.getAbsolutePath(),options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height;
            final int halfWidth = width;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    private String createFilename() {
        Date curDate = new Date();
        String curDateStr = String.valueOf(curDate.getTime());

        return curDateStr;
    }

    private String saveImage() {
        if (rotatedBitmap == null) {
            AppConstants.println("No picture to be saved.");
            return "";
        }

        File photoFolder = new File(AppConstants.FOLDER_PHOTO);

        if(!photoFolder.isDirectory()) {
            //Log.d(TAG, "creating photo folder : " + photoFolder);
            photoFolder.mkdirs();
        }

        String photoFilename = createFilename();
        String imagePath = photoFolder + File.separator + photoFilename;

        try {
            FileOutputStream outstream = new FileOutputStream(imagePath);
            rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
            outstream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return imagePath;
    }


    protected void getDateToday() {
        currentDate = new Date();
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd");

        etDate.setText(sdfYear.format(currentDate) + "년" + sdfMonth.format(currentDate) + "월"
                + sdfDay.format(currentDate) + "일");
    }


    protected void updateEditText() {
        StringBuffer sb = new StringBuffer();

        etDate.setText(sb.append(iYear + "년 ").append((iMonth+1) + "월 ").append(iDay + "일"));
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            iYear = year;
            iMonth = month;
            iDay = dayOfMonth;

            updateEditText();
        }
    };

    public void onDateTextClicked(View v) {
        String strDate = etDate.getText().toString();
        strDate = strDate.replace("년", "/").replace("월", "/")
                .replace("일", "/");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date pickDate = new Date(strDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(pickDate);
            Dialog dia = null;

            // strDate 값을 기본값으로 날짜 선택 다이얼로그 생성
            dia = new DatePickerDialog(getActivity(), dateSetListener, cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dia.show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    /**
     * 데이터베이스 레코드 수정
     */
    private void modifyData() {

        String imagePath = saveImage();

        // update note
        String sql = "update " + MovieDataBase.TABLE_NOTE +
                " set " +
                "   IMAGE = '" + imagePath + "'" +
                "   ,NAME = '" + etName.getText().toString() + "'" +
                "   ,DIRECTOR = '" + etDirector.getText().toString() + "'" +
                "   ,ACTOR = '" + etActor.getText().toString() + "'" +
                "   ,GENRE = '" + spGenre.getSelectedItem().toString() + "'" +
                "   ,RATING = '" + tv.getText().toString() + "'" +
                "   ,DATE = '" + etDate.getText().toString() + "'" +
                "   ,PLACE = '" + etPlace.getText().toString() + "'" +
                "   ,IMPRESSIVE_SENTENCE = '" + etImpressive.getText().toString() + "'" +
                "   ,REVIEW = '" + etReview.getText().toString() + "'" +
                " where " +
                "   _id = " + MovieId;

        //Log.d(TAG, "sql : " + sql);
        MovieDataBase database = MovieDataBase.getInstance(context);
        database.execSQL(sql);

    }


    /**
     * 레코드 삭제
     */
    private void deleteData() {
        AppConstants.println("삭제되었습니다");

        String sql = "delete from " + MovieDataBase.TABLE_NOTE +
                " where " +
                "   _id = " + MovieId;

        //Log.d(TAG, "sql : " + sql);
        MovieDataBase database = MovieDataBase.getInstance(context);
        database.execSQL(sql);
    }

    private void showSaveMsg()
    {
        final ViewItemActivity activity = (ViewItemActivity) getActivity();
        if(activity.isContextChanged) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            //builder.setTitle(null);
            builder.setMessage("리뷰를 수정하시겠습니까?");

            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    modifyData();
                    Toast.makeText(getActivity(), "저장되었습니다", Toast.LENGTH_SHORT).show();
                    activity.setResult(1);
                    activity.finish();
                }
            });
            builder.setNegativeButton("취소", null);

            builder.setCancelable(true);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            Toast.makeText(activity, "변경 사항이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    private void showDeleteMsg()
    {
        final ViewItemActivity activity = (ViewItemActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //builder.setTitle(null);
        builder.setMessage("리뷰를 삭제하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteData();
                Toast.makeText(getActivity(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
                activity.setResult(1);
                activity.finish();
            }
        });
        builder.setNegativeButton("취소", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showEndMsg()
    {
        final ViewItemActivity activity = (ViewItemActivity) getActivity();
        if(activity.isContextChanged) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            //builder.setTitle(null);
            builder.setMessage("작성하던 리뷰가 삭제됩니다. 작성을 취소하시겠습니까?");

            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    activity.finish();
                }
            });
            builder.setNegativeButton("취소", null);

            builder.setCancelable(true);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            activity.setResult(0);
            activity.finish();
        }
    }
}
