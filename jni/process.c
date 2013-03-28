#include "process.h"


jbyte* buffer;
jbyte *U;
jbyte *V;
int sizeY;

JNIEXPORT jintArray JNICALL Java_lt_neworld_arRegistration_Process_process
	(JNIEnv *env , jobject jo, jbyteArray initBuffer, jint size, jint width, jbyte _U, jbyte _V)

{
	int founded = 0;
	int rezPointer = 0;
	jint rez[MAX_BLOBS * 4];


	jboolean isCopy;
	buffer = (*env)->GetByteArrayElements(env, initBuffer, &isCopy);

	int workingSize = size / 4;
	sizeY = size / 2;

	U = &_U;
	V = &_V;

	jboolean checked[workingSize];

	int i, ii, index;
	checked[workingSize] = 0;

	__android_log_print(ANDROID_LOG_ERROR, "jni", "%d", size);

	for (i = 0; i < workingSize; i++) {
		checked[i] = 0;
	}

	__android_log_print(ANDROID_LOG_ERROR, "jni", "done");
	/*

	int minX, minY, maxX, maxY;
	Stack *steps = NULL;


	for (index = 0; index < workingSize; index += 2) {
		if (checked[index])
			continue;

		checked[index] = true;

		if (good(index)) {
			minX = maxX = index % width;
			minY = maxY = index / width;

			push(&steps, index);

			while (!empty(steps)) {
				i = pop(&steps);

				ii = i - width;
				if (ii >= 0 && !checked[ii] && good(ii)) {
					checked[ii] = true;
					push(&steps, ii);
					minY = min(minY, ii / width);
				}

				ii = i - 1;
				if (ii >= 0 && !checked[ii] && good(ii)) {
					checked[ii] = true;
					push(&steps, ii);
					minX = min(minX, ii % width);
				}

				ii += 2;
				if (ii < workingSize && !checked[ii] && good(ii)) {
					checked[ii] = true;
					push(&steps, ii);
					maxX = max(maxX, ii % width);
				}

				ii = i + width;
				if (ii < workingSize && !checked[ii] && good(ii)) {
					checked[ii] = true;
					push(&steps, ii);
					maxY = max(maxX, ii / width);
				}
			}

			rez[rezPointer++] = minX;
			rez[rezPointer++] = minY;
			rez[rezPointer++] = maxX;
			rez[rezPointer++] = maxY;

			if (++founded >= MAX_BLOBS)
				break;
		}
	}
	*/

	if (founded == 0)
		return NULL;

	jintArray result;

	result = (*env)->NewIntArray(env, founded * 4);

	(*env)->SetIntArrayRegion(env, result, 0, founded * 4, rez);
	return result;
}

void push(Stack **steps, int index) {
	Stack *node = malloc(sizeof(Stack));
	node->index = index;
	node->up = *steps;

	*steps = node;
}

int pop(Stack **steps) {
	if (steps == NULL)
		return 0;

	Stack *head = *steps;

	int rez = (*steps)->index;
	*steps = (*steps)->up;

	free(head);

	return rez;
}

inline jboolean empty(Stack *steps) {
	return steps == NULL;
}

inline jboolean good(int index) {
	int pos = sizeY + index * 2;
	return abs(buffer[pos] - *U) < TRESHOLD && abs(buffer[++pos] - *V) < TRESHOLD;
}

inline int min(int a, int b) {
	return a < b? a : b;
}

inline int max(int a, int b) {
	return a > b? a : b;
}
