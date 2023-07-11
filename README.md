Плавная gif анимация.

1. Создаем AnimationDrawable
2. Загружаем .gif файл, вытаскиваем каждый кадр с помощью GifDecoder (в интернете полно реализаций), и собственно добавляем эти кадры `addFrame(drawable, decoder.getDelay(i));`

Способы применения:
1. Для ImageView просто присваиваем и анимация работает автоматически: `imageView.setImageDrawable(gif);`
2. Для TextView необходимо:
- создать новый класс AnimatedImageSpan расширяющий DynamicDrawableSpan
- реализовать в новом классе пиналку (на основе Handler), которая заставит перерисовывать TextView
- сформировать Spanned разметку, вставить туда наш новый класс AnimatedImageSpan
- установить в качестве коллбэка TextView: `gif3.setCallback(textview);`
3. Для преобразования HTML в текст с gif анимацией необходимо:
- берем наш новый класс AnimatedImageSpan, и расширяем интерфейсами Runnable, Animatable, получаем MyGifDrawable
- расширяем Html.ImageGetter, для gif изображений используем новый MyGifDrawable

Альтернативный второй вариант - расширить класс TextView (назовем его HTMLTextView), который при присвоении текста вытащит все AnimatedImageSpan, и самостоятельно установит коллбэк `drawable.setCallback(HTMLTextView.this);`. Что удобно - можно ставить анимацию на паузу и запускать когда угодно.

Анимация не тормозит в ImageView, TextView, RecyclerView.

https://ru.stackoverflow.com/questions/1513858