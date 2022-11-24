# PresenterSlideView
PresenterSlideView is a custom carousel view for android, the implementation is nearly identical to that of the RecyclerView as this view inherits from it. This view is intended to be used as an easier to implement LayoutPager with built in dots indicator (with swappable drawables) and is also optionally capable of autoscroll.

<img src="https://raw.githubusercontent.com/NsMobileTeam/PresenterSlideView/main/art_readme/sample.gif" width=40%>

## Implementation
[![](https://jitpack.io/v/NsMobileTeam/PresenterSlideView.svg)](https://jitpack.io/#NsMobileTeam/PresenterSlideView)

**Step 1: Add jitpack.io as a repository for your project**

Option 1: In build.gradle **(Project)**:

```gradle
buildscript {
    repositories {
      ...
      maven { url 'https://jitpack.io' }
    }
  }

allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
<br/>

Option 2: In settings.gradle **(Project Settings)**:

```gradle
pluginManagement {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
<br/>

**Step 2: Implement this library module as a dependency for your project**

In build.gradle **(Module)**:

```gradle
  dependencies {
    ...
    implementation 'com.github.nsmobileteam:presenterslideview:1.0.0'
    ...
  }
```

## Usage

Adding this view in your layout xml file:
```xml
...
<com.nextsense.presenterslideview.PresenterSlideView
    android:id="@+id/svItems"
    android:layout_width="match_parent"
    android:layout_height="256dp"
    app:orientation="horizontal"
    app:loopItems="true"
    app:autoScroll="true"
    app:autoScrollDelay="3000"
    app:dotVisibility="visible"
    app:dotSize="16dp"
    app:dotMargin="2dp"
    app:dotSelected="@drawable/dot_selected"
    app:dotUnselected="@drawable/dot_unselected"/>
...
```
<br/>

Create an items adapter similar to RecyclerView.Adapter:
```java
...
class ItemAdapter: PresenterSlideView.Adapter<ItemAdapter.ItemViewHolder>() {
    private var itemList = arrayListOf<Int>()

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup): ItemViewHolder {
        val itemView = inflater.inflate(R.layout.item_view, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindView(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        //TODO populate this specific ViewHolder with item data and listeners of your choice
    }

    override fun getCount(): Int {
        return itemList.size
    }

    fun setItems(items: ArrayList<Int>) {
        this.itemList = items
        notifyDataSetChanged()
    }

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        //TODO init your view holder here
    }
}
...
```
<br/>

Create and add items to your Adapter and add it to your PresenterSlideView:
```java
...
    val adapter = ItemAdapter()
    adapter.setItems(itemObjects)
    val presenterSlideView = findViewById<PresenterSlideView>(R.id.svItems)
    presenterSlideView.setAdapter(adapter)
...
```

## License
[Apache License 2.0](https://github.com/NsMobileTeam/PresenterSlideView/blob/main/LICENSE)
