using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[RequireComponent(typeof(Rigidbody))]
public class PlayerController : MonoBehaviour
{
	public float JumpForce = 4f;
	public float MoveSpeed = 3f;
	
	private bool _grounded;
	private Rigidbody _rb;
	
	private void Start ()
	{
		_rb = gameObject.GetComponent<Rigidbody>();
	}

	private void OnCollisionStay(Collision other)
	{
		_grounded = true;
	}

	private void Update () 
	{
		if (Input.GetKeyDown(KeyCode.Space) && _grounded)
		{
			_rb.AddForce(0, JumpForce, 0, ForceMode.Impulse);
			_grounded = false;
		}

		var x = Input.GetAxis("Horizontal") * Time.deltaTime * MoveSpeed;
		var z = Input.GetAxis("Vertical") * Time.deltaTime * MoveSpeed;
		
		transform.Rotate(0, x, 0);
		transform.Translate(0, 0, z);
	}
}
