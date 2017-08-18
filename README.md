Fancy Menu
==========

Extension library written Kotlin that enables custom layouts in overflow menu.

Initial version provides ability to specify icon, full support for custom views is in works.

<img src=assets/example-menu.png width=335 height=305 />

Usage
-----

In Kotlin
```kotlin
import pl.khrone.libraries.fancymenu.prepareFancyMenu

class ExampleActivity: Activity()
{
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean
    {
        prepareFancyMenu(R.id.toolbar)
        return super.onPrepareOptionsMenu(menu)
    }
}
```

In Java
```java
public class ExampleActivity extends Activity
{

    @Override 
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        FancyMenuKt.prepareFancyMenu(this, R.id.toolbar, null, pl.khrone.libraries.fancymenu.R.layout.fancy_menu_item);
        return super.onPrepareOptionsMenu(menu);
    }

}
```

Where ```R.id.toolbar``` would be your Toolbar from layout.

Things to do
------------

* Custom views


Changelog
---------

[Current version is 0.1.0](CHANGELOG.md)


License
-------

    Copyright (C) 2017 Łukasz Milewski

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
