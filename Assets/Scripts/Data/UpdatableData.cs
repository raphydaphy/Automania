using UnityEngine;

public class UpdatableData : ScriptableObject
{
    public event System.Action OnValuesUpdated;
    public bool AutoUpdate;

    protected virtual void OnValidate()
    {
        if (AutoUpdate)
        {
            NotifyOfUpdates();
        }
    }

    public void NotifyOfUpdates()
    {
        if (OnValuesUpdated != null)
        {
            OnValuesUpdated();
        }
    }
}