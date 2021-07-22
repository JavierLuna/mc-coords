# Mc-Coords

A [Paper MC](https://papermc.io/) plugin to save and share coordinates with your minecraft buddies.

## Commands

### List coordinates

`/coords list`

That will give you a list of all the coordinates with their associated description.

There's no pagination implemented yet

### Add a coordinate

You have three ways of adding a coordinate:

```
# Save current coordinate
/coords Skeleton spawner

# Save specific coordinate
/coords 234 25 -254 Zombie spawner

# Save x z coordinate because you don't care about y
/coords 234 -254 Spider spawner
```

### Delete/Edit a coordinate

Not yet implemented sorry

## FAQ that no one asked, but I feel like answering

### Which versions of minecraft does this support?

I only tested in 1.17 and I stated that in the `plugin.yml` file. I didn't test older versions although it should work.
When I have time to test it I'll decrease the API version.

### How do I install this?

Compile this into a Jar and put that Jar into the `plugins` directory of your spigot/paper server. I wish I could
provide more detail but I don't do Java (I swear!) and I don't really understand what I am doing with the tooling half
of the time.