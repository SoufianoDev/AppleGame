#!/bin/bash

# ================= CONFIGURATION =================
PACKAGE_NAME="applegame"
GAME_DISPLAY_NAME="Apple Game"
VERSION="1.0"
ARCHITECTURE="amd64"
MAINTAINER="SoufianoDev <contact.soufianodev@gmail.com>"

# *** مسارات واضحة - الملفات التنفيذية هنا في releases/ ***
GAME_BINARY="AppleGame-Alpha-Version_Linux.x86_64"
GAME_PCK="AppleGame-Alpha-Version_Linux.pck"

# *** مسارات لأجزاء أخرى من المشروع ***
# '..' تعني "المجلد الأب" (أي مجلد applegame الرئيسي)
JRE_SOURCE_DIR="../jvm/jre-amd64-linux"
GAME_ICON="../src/main/resources/icons/AppleGame.png"

# *** مكان وضع الحزمة النهائية ***
OUTPUT_DIR="final"

# ================= بدء الإنشاء =================
echo "بدء إنشاء حزمة Debian للعبة $GAME_DISPLAY_NAME"
echo "المجلد الحالي: $(pwd)"
echo ""

# إنشاء مجلد الإخراج إذا لم يكن موجوداً
mkdir -p "$OUTPUT_DIR"

# التحقق من وجود الملفات الأساسية
echo "🔍 التحقق من الملفات:"

if [ ! -f "$GAME_BINARY" ]; then
    echo "❌ خطأ: لم يتم العثور على الملف التنفيذي '$GAME_BINARY'"
    echo "   الملفات الموجودة في المجلد الحالي:"
    ls -la *.x86_64 *.pck 2>/dev/null
    exit 1
fi
echo "✅ الملف التنفيذي: $GAME_BINARY"

[ -f "$GAME_PCK" ] && echo "✅ ملف البيانات: $GAME_PCK" || echo "⚠️  ملاحظة: ملف .pck غير منفصل (قد يكون مضمناً)"

if [ -d "$JRE_SOURCE_DIR" ]; then
    echo "✅ مجلد JRE: $JRE_SOURCE_DIR"
else
    echo "❌ خطأ: لم يتم العثور على مجلد JRE"
    echo "   حاول: ls -la $JRE_SOURCE_DIR"
    exit 1
fi

if [ -f "$GAME_ICON" ]; then
    echo "✅ أيقونة اللعبة: $GAME_ICON"
else
    echo "⚠️  ملاحظة: لم يتم العثور على أيقونة اللعبة"
fi

# ================= بناء الحزمة =================
echo ""
echo "📦 بناء هيكل الحزمة..."

PACKAGE_ROOT="${PACKAGE_NAME}_${VERSION}_${ARCHITECTURE}"
BUILD_DIR="/tmp/${PACKAGE_ROOT}"

# تنظيف أي نسخ سابقة
rm -rf "$BUILD_DIR"

# 1. إنشاء هيكل المجلدات
mkdir -p "$BUILD_DIR/opt/$PACKAGE_NAME" \
         "$BUILD_DIR/usr/share/applications" \
         "$BUILD_DIR/usr/games" \
         "$BUILD_DIR/DEBIAN"

# 2. نسخ ملفات اللعبة إلى /opt/applegame/
echo "  نسخ ملفات اللعبة..."

# الملفات الموجودة هنا في releases/
cp "$GAME_BINARY" "$BUILD_DIR/opt/$PACKAGE_NAME/"
[ -f "$GAME_PCK" ] && cp "$GAME_PCK" "$BUILD_DIR/opt/$PACKAGE_NAME/"

# مجلد JRE من المستوى الأعلى
cp -r "$JRE_SOURCE_DIR" "$BUILD_DIR/opt/$PACKAGE_NAME/"

# الأيقونة من المستوى الأعلى
if [ -f "$GAME_ICON" ]; then
    cp "$GAME_ICON" "$BUILD_DIR/opt/$PACKAGE_NAME/icon.png"
else
    # إنشاء أيقونة بديلة
    echo "  إنشاء أيقونة بديلة..."
    echo -e "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAACXBIWXMAAAsSAAALEgHS3X78AAAB" \
            "JElEQVR4nO3YsQ3DMAwEQc5Q/88aCiEDYSDvLtqJIvAtM7O6+wMAAAAAAADwF+/7/vqEf3rvVXef" \
            "usxM1V3VXVV3qu5U3dV9p+5O3Z26O3V36u7U3an7Tt2dujt1d+ru1N2pu1P3nbo7dXfq7tTdqbtT" \
            "d6fuO3V36u7U3am7U3en7k7dd+ru1N2pu1N3p+5O3Z2679TdqbtTd6fuTt2dujt136m7U3en7k7d" \
            "nbo7dXfqvlN3p+5O3Z26O3V36u7UfafuTt2dujt1d+ru1N2p+07dnbo7dXfq7tTdqbtT9526O3V3" \
            "6u7U3am7U3en7jt1d+ru1N2pu1N3p+5O3Xfq7tTdqbtTd6fuTt2duu/U3am7U3en7k7dnbo7dd+p" \
            "u1N3p+5O3Z26O3V36r5Td6fuTt2dujt1d+ru1H2n7k7dnbo7dXfq7tTdqftO3Z26O3V36u7U3am7" \
            "U/eduq9OAgAAAAAAAMBfPggHAGl+wLhZAAAAAElFTkSuQmCC" | base64 -d > "$BUILD_DIR/opt/$PACKAGE_NAME/icon.png" 2>/dev/null || \
    echo "PNG" > "$BUILD_DIR/opt/$PACKAGE_NAME/icon.png"
fi

# 3. إنشاء ملف التشغيل
echo "  إنشاء قائمة تشغيل..."
cat > "$BUILD_DIR/usr/games/$PACKAGE_NAME" << EOF
#!/bin/bash
# قائمة تشغيل للعبة $GAME_DISPLAY_NAME
cd "/opt/$PACKAGE_NAME"
exec "./$GAME_BINARY" "\$@"
EOF
chmod 755 "$BUILD_DIR/usr/games/$PACKAGE_NAME"

# 4. إنشاء اختصار سطح المكتب
echo "  إنشاء اختصار للتطبيقات..."
cat > "$BUILD_DIR/usr/share/applications/${PACKAGE_NAME}.desktop" << EOF
[Desktop Entry]
Type=Application
Name=$GAME_DISPLAY_NAME
Comment=لعبة مبنية باستخدام Godot وKotlin
Exec=/usr/games/$PACKAGE_NAME
Icon=/opt/$PACKAGE_NAME/icon.png
Terminal=false
Categories=Game;
Keywords=game;godot;
EOF

# 5. إنشاء ملف التحكم
echo "  إنشاء معلومات الحزمة..."
INSTALLED_SIZE=$(du -sk "$BUILD_DIR/opt" 2>/dev/null | cut -f1)
[ -z "$INSTALLED_SIZE" ] && INSTALLED_SIZE=102400

cat > "$BUILD_DIR/DEBIAN/control" << EOF
Package: $PACKAGE_NAME
Version: $VERSION
Architecture: $ARCHITECTURE
Maintainer: $MAINTAINER
Installed-Size: $INSTALLED_SIZE
Depends: libc6 (>= 2.31), libstdc++6 (>= 4.8.1)
Section: games
Priority: optional
Description: $GAME_DISPLAY_NAME
 لعبة كاملة مبنية باستخدام Godot Engine وKotlin/JVM.
 تحتوي على بيئة Java Runtime Environment (JRE) مضمّنة.
EOF

# 6. إنشاء سكريبت ما بعد التثبيت
cat > "$BUILD_DIR/DEBIAN/postinst" << 'EOF'
#!/bin/sh
# تحديث قاعدة بيانات تطبيقات سطح المكتب
command -v update-desktop-database >/dev/null 2>&1 && \
    update-desktop-database /usr/share/applications 2>/dev/null || true

echo ""
echo "✨ $GAME_DISPLAY_NAME تم تثبيتها بنجاح!"
echo "   يمكنك تشغيلها من قائمة التطبيقات أو بكتابة: $PACKAGE_NAME"
echo ""
EOF
chmod 755 "$BUILD_DIR/DEBIAN/postinst"

# 7. بناء الحزمة النهائية
echo "  بناء حزمة .deb..."
if command -v dpkg-deb >/dev/null 2>&1; then
    if dpkg-deb --build --root-owner-group "$BUILD_DIR" >/dev/null 2>&1; then
        mv "$BUILD_DIR.deb" "$OUTPUT_DIR/${PACKAGE_ROOT}.deb"
        
        echo ""
        echo "✅ تم إنشاء الحزمة بنجاح!"
        echo "   الموقع: $OUTPUT_DIR/${PACKAGE_ROOT}.deb"
        echo ""
        echo "📦 للتثبيت:"
        echo "   cd $OUTPUT_DIR"
        echo "   sudo apt install ./${PACKAGE_ROOT}.deb"
        echo ""
        echo "🎮 للتشغيل:"
        echo "   $PACKAGE_NAME"
        echo ""
        echo "🗑️  للإزالة:"
        echo "   sudo apt remove $PACKAGE_NAME"
    else
        echo "❌ فشل بناء الحزمة."
    fi
else
    echo "❌ الأداة dpkg-deb غير مثبتة."
    echo "   قم بتثبيتها: sudo apt install dpkg"
    exit 1
fi

# تنظيف الملفات المؤقتة
rm -rf "$BUILD_DIR"
echo "تم الإنشاء!"
