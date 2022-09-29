import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'pdf_bitmaps_platform_interface.dart';

/// An implementation of [PdfBitmapsPlatform] that uses method channels.
class MethodChannelPdfBitmaps extends PdfBitmapsPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('pdf_bitmaps');

  @override
  Future<int?> pdfPageCount({required String pdfUri}) async {
    final pageCount = await methodChannel
        .invokeMethod<int?>('pdfPageCount', {'pdfUri': pdfUri});
    return pageCount;
  }

  @override
  Future<Uint8List?> pdfBitmap({PDFBitmapParams? params}) async {
    final Uint8List? bytes = await methodChannel.invokeMethod<Uint8List?>(
        'pdfBitmap', params?.toJson());
    return bytes;
  }
}

/// Parameters for the [mergePDFs] method.
class PDFBitmapParams {
  /// Provide uris of pdf files to merge.
  final String pdfUri;

  /// Provide pdf page index for which you want bitmap.
  final int pageIndex;

  /// Provide pdf page bitmap quality from 1 to 100.
  final int quality;

  /// Create parameters for the [mergePDFs] method.
  const PDFBitmapParams(
      {required this.pdfUri, required this.pageIndex, this.quality = 100})
      : assert(quality > 0 || quality <= 100,
            'quality should be between 1 to 100');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfUri': pdfUri,
      'pageIndex': pageIndex,
      'quality': quality,
    };
  }
}
