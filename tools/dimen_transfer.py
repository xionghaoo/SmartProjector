# 1、请将该程序放到app/src/main/res文件目录下运行
# 2、该程序是将现有xxxhdpi dimens文件转换，自动生成xxhdpi和xhdpi的dimens文件

import re
import os

# UI设计以iPhone plus的屏幕为标准设计，
# 换算成Android屏幕，比较接近的为2K屏，xxxhdpi屏幕
UI_SCREEN_SCALE = 3.5
UI_SCREEN_WIDTH = 1440.0

# 1K屏，市面上大部分的手机都采用这种屏幕
XXHDPI_SCREEN_SCALE = 3.0
XXHDPI_SCREEN_WIDTH = 1080.0

XHDPI_SCREEN_SCALE = 2.0
XHDPI_SCREEN_WIDTH = 720.0

dimen_type_xxxhdpi = 'values-xxxhdpi'
dimen_type_xxhdpi = 'values-xxhdpi'
dimen_type_xhdpi = 'values-xhdpi'

INPUT_FILE_NAME = 'values/dimens.xml'
OUTPUT_FILE_NAME = 'dimens_transfer.xml'


def create_dimen_file_from(xxxhdpi_file, dimen_type):
    folder = r'{0}'.format(dimen_type_xxxhdpi)
    if dimen_type == dimen_type_xxxhdpi:
        folder = r'{0}'.format(dimen_type_xxxhdpi)
    elif dimen_type == dimen_type_xxhdpi:
        folder = r'{0}'.format(dimen_type_xxhdpi)
    elif dimen_type == dimen_type_xhdpi:
        folder = r'{0}'.format(dimen_type_xhdpi)

    if not os.path.exists(folder):
        os.mkdir(folder)

    f = open('{0}/{1}'.format(folder, OUTPUT_FILE_NAME), 'w')

    f.write('<resources>\n')

    f.write('\t<!--sp transfer from xxxhdpi-->\n')
    lines = [line for line in open(xxxhdpi_file)]
    for l in lines:
        sp_line = re.match(r'.*name=["](.*)["]>(.*)sp.*', l)
        if sp_line:
            dimen_name = sp_line.group(1)
            if float(sp_line.group(2)).is_integer() and int(sp_line.group(2)) > 1:
                dimen_value = get_dpi_size(int(sp_line.group(2)), dimen_type)
                f.write('\t<dimen name="{0}">{1}sp</dimen>\n'.format(dimen_name, dimen_value))

    f.write('\n\t<!--dp transfer from xxxhdpi-->\n')
    for l in lines:
        sp_line = re.match(r'.*name=["](.*)["]>(.*)dp.*', l)
        if sp_line:
            dimen_name = sp_line.group(1)
            if float(sp_line.group(2)).is_integer() and int(sp_line.group(2)) > 1:
                dimen_value = get_dpi_size(int(sp_line.group(2)), dimen_type)
                f.write('\t<dimen name="{0}">{1}dp</dimen>\n'.format(dimen_name, dimen_value))

    f.write('</resources>\n')
    f.close()


def get_dpi_size(size, dimen_type):
    if dimen_type == dimen_type_xxxhdpi:
        return size
    elif dimen_type == dimen_type_xxhdpi:
        return round(size * UI_SCREEN_SCALE / UI_SCREEN_WIDTH * XXHDPI_SCREEN_WIDTH / XXHDPI_SCREEN_SCALE)
    elif dimen_type == dimen_type_xhdpi:
        return round(size * UI_SCREEN_SCALE / UI_SCREEN_WIDTH * XHDPI_SCREEN_WIDTH / XHDPI_SCREEN_SCALE)
    else:
        return 0


def transfer_xxxhdpi_dimen(xxxhdpi_file):
    create_dimen_file_from(xxxhdpi_file, dimen_type_xxxhdpi)
    create_dimen_file_from(xxxhdpi_file, dimen_type_xxhdpi)
    create_dimen_file_from(xxxhdpi_file, dimen_type_xhdpi)


transfer_xxxhdpi_dimen(INPUT_FILE_NAME)

