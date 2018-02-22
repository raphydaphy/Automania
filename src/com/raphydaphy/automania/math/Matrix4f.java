package com.raphydaphy.automania.math;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class Matrix4f
{

	private float m00, m01, m02, m03;
	private float m10, m11, m12, m13;
	private float m20, m21, m22, m23;
	private float m30, m31, m32, m33;

	public Matrix4f()
	{
		setIdentity();
	}

	public static Matrix4f orthographic(float left, float right, float bottom, float top, float near, float far)
	{
		Matrix4f ortho = new Matrix4f();

		float tx = -(right + left) / (right - left);
		float ty = -(top + bottom) / (top - bottom);
		float tz = -(far + near) / (far - near);

		ortho.m00 = 2f / (right - left);
		ortho.m11 = 2f / (top - bottom);
		ortho.m22 = -2f / (far - near);
		ortho.m03 = tx;
		ortho.m13 = ty;
		ortho.m23 = tz;

		return ortho;
	}

	public static Matrix4f frustum(float left, float right, float bottom, float top, float near, float far)
	{
		Matrix4f frustum = new Matrix4f();

		float a = (right + left) / (right - left);
		float b = (top + bottom) / (top - bottom);
		float c = -(far + near) / (far - near);
		float d = -(2f * far * near) / (far - near);

		frustum.m00 = (2f * near) / (right - left);
		frustum.m11 = (2f * near) / (top - bottom);
		frustum.m02 = a;
		frustum.m12 = b;
		frustum.m22 = c;
		frustum.m32 = -1f;
		frustum.m23 = d;
		frustum.m33 = 0f;

		return frustum;
	}

	public static Matrix4f perspective(float fovy, float aspect, float near, float far)
	{
		Matrix4f perspective = new Matrix4f();

		float f = (float) (1f / Math.tan(Math.toRadians(fovy) / 2f));

		perspective.m00 = f / aspect;
		perspective.m11 = f;
		perspective.m22 = (far + near) / (near - far);
		perspective.m32 = -1f;
		perspective.m23 = (2f * far * near) / (near - far);
		perspective.m33 = 0f;

		return perspective;
	}

	public static Matrix4f translate(float x, float y, float z)
	{
		Matrix4f translation = new Matrix4f();

		translation.m03 = x;
		translation.m13 = y;
		translation.m23 = z;

		return translation;
	}

	public static Matrix4f scale(float x, float y, float z)
	{
		Matrix4f scaling = new Matrix4f();

		scaling.m00 = x;
		scaling.m11 = y;
		scaling.m22 = z;

		return scaling;
	}

	public final void setIdentity()
	{
		m00 = 1f;
		m11 = 1f;
		m22 = 1f;
		m33 = 1f;

		m01 = 0f;
		m02 = 0f;
		m03 = 0f;
		m10 = 0f;
		m12 = 0f;
		m13 = 0f;
		m20 = 0f;
		m21 = 0f;
		m23 = 0f;
		m30 = 0f;
		m31 = 0f;
		m32 = 0f;
	}

	public Matrix4f add(Matrix4f other)
	{
		Matrix4f result = new Matrix4f();

		result.m00 = this.m00 + other.m00;
		result.m10 = this.m10 + other.m10;
		result.m20 = this.m20 + other.m20;
		result.m30 = this.m30 + other.m30;

		result.m01 = this.m01 + other.m01;
		result.m11 = this.m11 + other.m11;
		result.m21 = this.m21 + other.m21;
		result.m31 = this.m31 + other.m31;

		result.m02 = this.m02 + other.m02;
		result.m12 = this.m12 + other.m12;
		result.m22 = this.m22 + other.m22;
		result.m32 = this.m32 + other.m32;

		result.m03 = this.m03 + other.m03;
		result.m13 = this.m13 + other.m13;
		result.m23 = this.m23 + other.m23;
		result.m33 = this.m33 + other.m33;

		return result;
	}

	/**
	 * Negates this matrix.
	 *
	 * @return Negated matrix
	 */
	public Matrix4f negate()
	{
		return multiply(-1f);
	}

	public Matrix4f subtract(Matrix4f other)
	{
		return this.add(other.negate());
	}

	public Matrix4f multiply(float scalar)
	{
		Matrix4f result = new Matrix4f();

		result.m00 = this.m00 * scalar;
		result.m10 = this.m10 * scalar;
		result.m20 = this.m20 * scalar;
		result.m30 = this.m30 * scalar;

		result.m01 = this.m01 * scalar;
		result.m11 = this.m11 * scalar;
		result.m21 = this.m21 * scalar;
		result.m31 = this.m31 * scalar;

		result.m02 = this.m02 * scalar;
		result.m12 = this.m12 * scalar;
		result.m22 = this.m22 * scalar;
		result.m32 = this.m32 * scalar;

		result.m03 = this.m03 * scalar;
		result.m13 = this.m13 * scalar;
		result.m23 = this.m23 * scalar;
		result.m33 = this.m33 * scalar;

		return result;
	}

	public Matrix4f multiply(Matrix4f other)
	{
		Matrix4f result = new Matrix4f();

		result.m00 = this.m00 * other.m00 + this.m01 * other.m10 + this.m02 * other.m20 + this.m03 * other.m30;
		result.m10 = this.m10 * other.m00 + this.m11 * other.m10 + this.m12 * other.m20 + this.m13 * other.m30;
		result.m20 = this.m20 * other.m00 + this.m21 * other.m10 + this.m22 * other.m20 + this.m23 * other.m30;
		result.m30 = this.m30 * other.m00 + this.m31 * other.m10 + this.m32 * other.m20 + this.m33 * other.m30;

		result.m01 = this.m00 * other.m01 + this.m01 * other.m11 + this.m02 * other.m21 + this.m03 * other.m31;
		result.m11 = this.m10 * other.m01 + this.m11 * other.m11 + this.m12 * other.m21 + this.m13 * other.m31;
		result.m21 = this.m20 * other.m01 + this.m21 * other.m11 + this.m22 * other.m21 + this.m23 * other.m31;
		result.m31 = this.m30 * other.m01 + this.m31 * other.m11 + this.m32 * other.m21 + this.m33 * other.m31;

		result.m02 = this.m00 * other.m02 + this.m01 * other.m12 + this.m02 * other.m22 + this.m03 * other.m32;
		result.m12 = this.m10 * other.m02 + this.m11 * other.m12 + this.m12 * other.m22 + this.m13 * other.m32;
		result.m22 = this.m20 * other.m02 + this.m21 * other.m12 + this.m22 * other.m22 + this.m23 * other.m32;
		result.m32 = this.m30 * other.m02 + this.m31 * other.m12 + this.m32 * other.m22 + this.m33 * other.m32;

		result.m03 = this.m00 * other.m03 + this.m01 * other.m13 + this.m02 * other.m23 + this.m03 * other.m33;
		result.m13 = this.m10 * other.m03 + this.m11 * other.m13 + this.m12 * other.m23 + this.m13 * other.m33;
		result.m23 = this.m20 * other.m03 + this.m21 * other.m13 + this.m22 * other.m23 + this.m23 * other.m33;
		result.m33 = this.m30 * other.m03 + this.m31 * other.m13 + this.m32 * other.m23 + this.m33 * other.m33;

		return result;
	}

	/**
	 * Transposes this matrix.
	 *
	 * @return Transposed matrix
	 */
	public Matrix4f transpose()
	{
		Matrix4f result = new Matrix4f();

		result.m00 = this.m00;
		result.m10 = this.m01;
		result.m20 = this.m02;
		result.m30 = this.m03;

		result.m01 = this.m10;
		result.m11 = this.m11;
		result.m21 = this.m12;
		result.m31 = this.m13;

		result.m02 = this.m20;
		result.m12 = this.m21;
		result.m22 = this.m22;
		result.m32 = this.m23;

		result.m03 = this.m30;
		result.m13 = this.m31;
		result.m23 = this.m32;
		result.m33 = this.m33;

		return result;
	}

	public void toBuffer(FloatBuffer buffer)
	{
		buffer.put(m00).put(m10).put(m20).put(m30);
		buffer.put(m01).put(m11).put(m21).put(m31);
		buffer.put(m02).put(m12).put(m22).put(m32);
		buffer.put(m03).put(m13).put(m23).put(m33);
		buffer.flip();
	}

	public FloatBuffer getBuffer() {
		FloatBuffer buffer = MemoryUtil.memAllocFloat(16);
		buffer.put(m00).put(m10).put(m20).put(m30);
		buffer.put(m01).put(m11).put(m21).put(m31);
		buffer.put(m02).put(m12).put(m22).put(m32);
		buffer.put(m03).put(m13).put(m23).put(m33);
		buffer.flip();
		return buffer;
	}

}