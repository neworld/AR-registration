#include "process.h"

JNIEXPORT jintArray JNICALL Java_lt_neworld_arRegistration_Process_process
	(JNIEnv *env , jobject jo, jbyteArray buffer, jint size, jint width, jbyte U, jbyte V)

{
	int founded = 0;
	jint rez[400];

	int workingSize = size / 4;
	jboolean checked[workingSize];

	int i;
	for (i = 0; i < workingSize; i++)
		checked[i] = false;

	int minX, minY, maxX, maxY;

	struct IndexStack *steps = NULL;



	jintArray result;
	result = (*env)->NewIntArray(env, 400);

	(*env)->SetIntArrayRegion(env, result, 0, 400, rez);
	return result;
}

void addStep(struct IndexStack **steps ) {

}
