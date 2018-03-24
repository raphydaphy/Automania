package main.java.com.raphydaphy.automania.render;

import main.java.com.raphydaphy.automania.models.IModel;

public class ModelTransform
{
    private Transform transform;
    private IModel model;

    public ModelTransform(Transform transform, IModel model)
    {
        this.transform = transform;
        this.model = model;
    }

    public Transform getTransform()
    {
        return transform;
    }

    public IModel getModel()
    {
        return model;
    }
}
