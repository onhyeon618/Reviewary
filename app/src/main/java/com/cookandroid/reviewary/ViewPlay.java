package com.cookandroid.reviewary;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
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

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class ViewPlay extends Fragment {

    Context context;

    EditText etName, etActor, etImpressive, etReview, etDate, etPlace;
    Date currentDate;
    int iYear, iMonth, iDay;
    Spinner spGenre;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;

    int PlayId = 0;

    RatingBar rb;
    TextView tv;

    // 이미지
    ImageButton imageButton;

    boolean isPhotoCaptured;
    boolean isPhotoFileSaved;
    boolean isPhotoCanceled;

    int selectedPhotoMenu;

    File file;
    Bitmap resultPhotoBitmap;

    Play item;

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
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_view_play, container, false);

        ViewItemActivity activity = (ViewItemActivity) getActivity();

        initUI(rootView);

        PlayId = activity.getMyData();

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDateTextClicked(rootView);
            }
        });

        applyItem();

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
        etActor = rootView.findViewById(R.id.etActor);
        etPlace = rootView.findViewById(R.id.etPlace);
        spGenre = rootView.findViewById(R.id.spGenre);

        // Rating Bar
        rb = (RatingBar) rootView.findViewById(R.id.ratingBar);
        tv = (TextView) rootView.findViewById(R.id.tvRatingNum);

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tv.setText(String.valueOf(rating));
            }
        });

        // 독서 완료일
        etDate = (EditText) rootView.findViewById(R.id.etDate);
        //      getDateToday();


        // 장르 Spinner
        arrayList = new ArrayList<>();
        arrayList.add("코믹");
        arrayList.add("감동");
        arrayList.add("드라마");
        arrayList.add("추리");
        arrayList.add("공포");
        arrayList.add("이인극");
        arrayList.add("독백극");
        arrayList.add("서사극");
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

                Intent intent = new Intent();
                intent.putExtra("state", "clear");
                getActivity().setResult(RESULT_OK, intent);

                showSaveMsg();

                modifyData();

            }
        });

        // 삭제 버튼
        deleteBtn = (Button) rootView.findViewById(R.id.btnDelete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("state", "clear");
                getActivity().setResult(RESULT_OK, intent);

                showDeleteMsg();

                deleteData();
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



    public void setImage(String imagePath, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        resultPhotoBitmap = BitmapFactory.decodeFile(imagePath, options);

        imageButton.setImageBitmap(resultPhotoBitmap);
    }

    public void applyItem() {
        AppConstants.println("applyItem called.");

        String sql = "select _id, IMAGE, NAME, ACTOR, GENRE, RATING, DATE, PLACE, IMPRESSIVE_SENTENCE, REVIEW from " + PlayDataBase.TABLE_NOTE + " where " +
                "   _id = " + PlayId;

        PlayDataBase database = PlayDataBase.getInstance(context);
        Cursor outCursor = database.rawQuery(sql);

        outCursor.moveToNext();

        int _id = outCursor.getInt(0);
        String imagePath = outCursor.getString(1);
        String name = outCursor.getString(2);
        String actor = outCursor.getString(3);
        String genre = outCursor.getString(4);
        String rating = outCursor.getString(5);
        String readDate = outCursor.getString(6);
        String place = outCursor.getString(7);
        String impressiveSentence = outCursor.getString(8);
        String review = outCursor.getString(9);

        etName.setText(name);
        etActor.setText(actor);
        etPlace.setText(place);
        etDate.setText(readDate);
        etImpressive.setText(impressiveSentence);
        etReview.setText(review);
        tv.setText(rating);
        rb.setRating(Float.parseFloat(rating));

        spGenre.setSelection(arrayList.indexOf(genre));

        if (imagePath == null || imagePath.equals("")) {
            imageButton.setImageResource(R.drawable.noimagefound);
        } else {
            setImage(item.getImage(), 1);
        }
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

                            imageButton.setImageResource(R.drawable.image_upload_icon);
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
        if (file == null) {
            file = createFile();
        }

        Uri fileUri = FileProvider.getUriForFile(context,"com.cookandroid.reviewary.fileprovider", file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            startActivityForResult(intent, AppConstants.REQ_PHOTO_CAPTURE);
        }
    }

    private File createFile() {
        String filename = "capture.jpg";
        File storageDir = Environment.getExternalStorageDirectory();
        File outFile = new File(storageDir, filename);

        return outFile;
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

        if (intent != null) {
            switch (requestCode) {
                case AppConstants.REQ_PHOTO_CAPTURE:  // 사진 찍는 경우
                    //Log.d(TAG, "onActivityResult() for REQ_PHOTO_CAPTURE.");

                    //Log.d(TAG, "resultCode : " + resultCode);

                    //setPicture(file.getAbsolutePath(), 8);
                    resultPhotoBitmap = decodeSampledBitmapFromResource(file, imageButton.getWidth(), imageButton.getHeight());
                    imageButton.setImageBitmap(resultPhotoBitmap);

                    break;

                case AppConstants.REQ_PHOTO_SELECTION:  // 사진을 앨범에서 선택하는 경우
                    //Log.d(TAG, "onActivityResult() for REQ_PHOTO_SELECTION.");

                    Uri selectedImage = intent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    resultPhotoBitmap = decodeSampledBitmapFromResource(new File(filePath), imageButton.getWidth(), imageButton.getHeight());
                    imageButton.setImageBitmap(resultPhotoBitmap);
                    isPhotoCaptured = true;

                    break;

            }
        }
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
        if (resultPhotoBitmap == null) {
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
            resultPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
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
        String sql = "update " + PlayDataBase.TABLE_NOTE +
                " set " +
                "   IMAGE = '" + imagePath + "'" +
                "   ,NAME = '" + etName.getText().toString() + "'" +
                "   ,ACTOR = '" + etActor.getText().toString() + "'" +
                "   ,GENRE = '" + spGenre.getSelectedItem().toString() + "'" +
                "   ,RATING = '" + tv.getText().toString() + "'" +
                "   ,DATE = '" + etDate.getText().toString() + "'" +
                "   ,PLACE = '" + etPlace.getText().toString() + "'" +
                "   ,IMPRESSIVE_SENTENCE = '" + etImpressive.getText().toString() + "'" +
                "   ,REVIEW = '" + etReview.getText().toString() + "'" +
                " where " +
                "   _id = " + PlayId;

                //Log.d(TAG, "sql : " + sql);
        PlayDataBase database = PlayDataBase.getInstance(context);
        database.execSQL(sql);


    }


    /**
     * 레코드 삭제
     */
    private void deleteData() {
        AppConstants.println("삭제되었습니다");

        String sql = "delete from " + PlayDataBase.TABLE_NOTE +
                    " where " +
                    "   _id = " + PlayId;

        //Log.d(TAG, "sql : " + sql);
        PlayDataBase database = PlayDataBase.getInstance(context);
        database.execSQL(sql);
    }

    private void showSaveMsg()
    {
        final ViewItemActivity activity = (ViewItemActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //builder.setTitle(null);
        builder.setMessage("편집한 리뷰가 저장됩니다. 리뷰를 저장하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "저장되었습니다", Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        });
        builder.setNegativeButton("취소", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showDeleteMsg()
    {
        final ViewItemActivity activity = (ViewItemActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //builder.setTitle(null);
        builder.setMessage("리뷰가 삭제됩니다. 리뷰를 삭제하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
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
}
