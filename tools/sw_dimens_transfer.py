# 根据默认的dimens.xml文件生成sw尺寸文件
# 1. 需要知道目标屏幕的宽度(px)和屏幕缩放因子
# 2. 计算方式是dp的百分比尺寸

import re
import os

# 默认dimens.xml文件的缩放因子和屏幕宽度
UI_SCREEN_SCALE = 1
UI_SCREEN_WIDTH = 1080

# 目标屏幕宽度(px)
sw_dp_list = [1080, 720, 411, 360, 752, 552]
# 默认的dimens.xml文件目录
res_path = '../app/src/main/res'
# res_path = '.'
values_folder = 'values'
dimens_file = 'dimens.xml'


# 创建适配尺寸文件
def create_dimen_file(target_dp):
    folder = r'{0}'.format('{0}/values-sw{1}dp'.format(res_path, target_dp))
    if not os.path.exists(folder):
        os.mkdir(folder)
    f = open('{0}/{1}'.format(folder, dimens_file), 'w')
    f.write('<resources>\n')
    # 遍历默认dimens文件的尺寸
    lines = [line for line in open('{0}/{1}/{2}'.format(res_path, "values", dimens_file))]
    # 写入sp尺寸
    f.write('\t<!--sp transfer from default-->\n')
    for l in lines:
        sp_line = re.match(r'.*name=["](.*)["]>(.*)sp.*', l)
        if sp_line:
            dimen_name = sp_line.group(1)
            sp_value = round(float(sp_line.group(2)), 2)
            if float(sp_line.group(2)).is_integer() and sp_value > 1:
                dimen_value = get_dpi_size(sp_value, target_dp)
                f.write('\t<dimen name="{0}">{1:.2f}sp</dimen>\n'.format(dimen_name, dimen_value))
    # 写入dp尺寸
    f.write('\n\t<!--dp transfer from default-->\n')
    for l in lines:
        dp_line = re.match(r'.*name=["](.*)["]>(.*)dp.*', l)
        if dp_line:
            dimen_name = dp_line.group(1)
            dp_value = round(float(dp_line.group(2)), 2)
            if float(dp_line.group(2)).is_integer() and dp_value > 1:
                dimen_value = get_dpi_size(dp_value, target_dp)
                f.write('\t<dimen name="{0}">{1:.2f}dp</dimen>\n'.format(dimen_name, dimen_value))
    f.write('</resources>\n')
    print("已生成：" + f.name)
    f.close()


# 计算适配尺寸
def get_dpi_size(size, target_dp):
    return round(size * UI_SCREEN_SCALE / UI_SCREEN_WIDTH * target_dp, 2)


# 适配尺寸迁移
def transfer():
    if not os.path.exists('{0}/{1}/{2}'.format(res_path, values_folder, dimens_file)):
        print('错误: values/dimens.xml文件不存在')
        return
    for index, dp in enumerate(sw_dp_list):
        create_dimen_file(dp)


# 开始尺寸迁移
transfer()

