//
// Created by Student on 6/14/2022.
//
#include "milan_kapetanovic_memorygame_JNIExample.h"

extern "C" JNIEXPORT jdouble JNICALL Java_milan_kapetanovic_memorygame_JNIExample_racunajProcenat
  (JNIEnv * env, jobject jobj, jdouble x)
  {
       x = (100 * x)/ 40;

       return x;
  }
